from sboxes import S0, S1, S2, S3
import sys
import itertools
import math
import time
from operator import ixor
import array

def bitArrayToBytes(bitArray):
    return [sum([byte[b] << b for b in range(0,8)])
            for byte in zip(*(iter(bitArray),) * 8)]

def tobits(s):
    result = []
    for c in s:
        bits = bin(ord(c))[2:]
        bits = '00000000'[len(bits):] + bits
        result.extend([int(b) for b in bits])
    return result

def frombits(bits):
    chars = []
    for b in range(len(bits) // 8):
        byte = bits[b*8:(b+1)*8]
        chars.append(chr(int(''.join([str(bit) for bit in byte]), 2)))
    return ''.join(chars)

def intToBitArray(input):
    s = "{0:b}".format(input)
    res = []
    for c in s:
        res.append(int(c))
    return res   

def chunkInput(bytesInput):

    messageLength = len(bytesInput)
    bytesInput.append(0x01)

    while(len(bytesInput) % 64 != 56):
        bytesInput.append(0x00)
    
    messageLength64 = bytes([messageLength]) * 8
    bytesInput += messageLength64


    chunks = []
    amountOfChunks = math.ceil(len(bytesInput) / 64)
    for i in range(int(amountOfChunks)):
        chunks.append(bytesInput[i*64:(i+1)*64])

    return chunks

def Fround(a, b, c, Wi, m):

    (a,b,c) = fm(a,b,c, Wi[0], m)
    (b,c,a) = fm(b,c,a, Wi[1], m)
    (c,a,b) = fm(c,a,b, Wi[2], m)
    (a,b,c) = fm(a,b,c, Wi[3], m)
    (b,c,a) = fm(b,c,a, Wi[4], m)
    (c,a,b) = fm(c,a,b, Wi[5], m)
    (a,b,c) = fm(a,b,c, Wi[6], m)
    (b,c,a) = fm(b,c,a, Wi[7], m)
    return (a, b, c)

def fm(a, b, c, x, m):

    # c = c ^ x
    # a = a - S0[((c) >> (0*8))&0xFF] ^ S1[((c) >> ( 2*8)) & 0xFF] ^ S2[((c) >> (4*8))&0xFF] ^ S3[((c) >> ( 6*8)) & 0xFF]
    # b = b + S3[((c) >> (1*8))&0xFF] ^ S2[((c) >> ( 3*8)) & 0xFF] ^ S1[((c) >> (5*8))&0xFF] ^ S0[((c) >> ( 7*8)) & 0xFF]
    # b = b * m


    # Dit stukje is een voorbeeld gevonden op internet.
    c ^= x
    c &= 0xffffffffffffffff
    a -= S0[((c) >> (0*8))&0xFF] ^ S1[((c) >> ( 2*8)) & 0xFF] ^ S2[((c) >> (4*8))&0xFF] ^ S3[((c) >> ( 6*8)) & 0xFF]
    b += S3[((c) >> (1*8))&0xFF] ^ S2[((c) >> ( 3*8)) & 0xFF] ^ S1[((c) >> (5*8))&0xFF] ^ S0[((c) >> ( 7*8)) & 0xFF]
    b *= m
    a &= 0xffffffffffffffff
    b &= 0xffffffffffffffff
    c &= 0xffffffffffffffff
    return (a,b,c)

def keySchedule(x):

    # allf = 0xFFFFFFFFFFFFFFFF
    # x[0] = x[0] - (x[7] ^ 0xA5A5A5A5A5A5A5A5)
    # x[1] = x[1] ^ x[0]
    # x[2] = (x[2] + x[1])

    # # X3 is X3 min X2 XOR met de binary complement van X1 << 19 
    # x[3] = x[3] - (x[2] ^ ((~x[1]&allf) << 19))
    # x[4] = x[4] ^ x[3]
    # x[5] = x[5] + x[4]
    # x[6] = x[6] - (x[5] ^ ((~x[4]&allf) >> 23))
    # x[7] = x[7] ^ x[6]
    
    # x[0] = x[0] + x[7]
    # x[1] = x[1] - (x[0] ^ ((~x[7]&allf) << 19))
    # x[2] = x[2] ^ x[1]
    # x[3] = x[3] + x[2]
    # x[4] = x[4] - (x[3] ^ ((~x[2]&allf) >> 23))
    # x[5] = x[5] ^ x[4]
    # x[6] = x[6] + x[5]
    # x[7] = x[7] - (x[6] ^ 0x0123456789ABCDEF)


    # Dit stukje is een voorbeeld gevonden op internet.
    allf = 0xFFFFFFFFFFFFFFFF
    x[0] = (x[0] - (x[7] ^ 0xA5A5A5A5A5A5A5A5)&allf ) & allf
    x[1] ^= x[0]
    x[2] = (x[2] + x[1]) & allf
    x[3] = (x[3] - (x[2] ^ (~x[1]&allf) << 19)&allf) & allf
    x[4] ^= x[3]
    x[5] = (x[5] + x[4]) & allf
    x[6] = (x[6] - (x[5] ^ (~x[4]&allf) >> 23)&allf) & allf
    x[7] ^= x[6]
    x[0] = (x[0] + x[7]) & allf
    x[1] = (x[1] - (x[0] ^ (~x[7]&allf) << 19)&allf) & allf
    x[2] ^= x[1]
    x[3] = (x[3] + x[2]) & allf
    x[4] = (x[4] - (x[3] ^ (~x[2]&allf) >> 23)&allf) & allf
    x[5] ^= x[4] 
    x[6] = (x[6] + x[5]) & allf
    x[7] = (x[7] - (x[6] ^ 0x0123456789ABCDEF)&allf ) & allf
    return x

def outerRound(Wi):

    Wi= Wi.decode("utf-8")
    Wi = tobits(Wi)
    Wi = bitArrayToBytes(Wi)

    # initial values a, b and c
    a = 0x0123456789ABCDEF
    b = 0xFEDCBA9876543210
    c = 0xF096A5B4C3B2E187

    (a, b, c) = Fround(a, b, c, Wi, 5)

    Wi = keySchedule(Wi)
    (a, b, c) = Fround(c, a, b, Wi, 7)

    Wi = keySchedule(Wi)
    (a, b, c) = Fround(b, c, a, Wi, 9)

    return (a,b,c)

def main():

    inputbytes = bytearray(input(), "utf-8")

    X = chunkInput(inputbytes)

    for chunk in X:
        (a,b,c) = outerRound(chunk)

    result = "%016X%016X%016X" % (a, b, c)
    print(result)   
 
 
main()
