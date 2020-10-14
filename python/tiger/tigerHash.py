from sboxes import S0, S1, S2, S3
import struct
import math

def chunkInput(bytesInput):

    messageLength = len(bytesInput)
    bytesInput.append(0x01)

    while(len(bytesInput) % 64 != 56):
        bytesInput.append(0x00)
    
    messageLength64 = (messageLength * 8).to_bytes(8, byteorder='big')
    bytesInput += messageLength64

    chunks = []
    amountOfChunks = math.ceil(len(bytesInput) / 64)
    for i in range(int(amountOfChunks)):
        chunks.append(bytesInput[i*64:(i+1)*64])

    return chunks

def bit8Tobit64(X):
    res = []
    amountOfItems = len(X) / 8
    for i in range(int(amountOfItems)):
        value = int.from_bytes(X[i*8:(i+1)*8], byteorder='big')
        res.append(int(value))
    return res

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

def outerRound(Wi, a, b, c):
    Wi = bit8Tobit64(Wi)

    (a, b, c) = Fround(a, b, c, Wi, 5)

    Wi = keySchedule(Wi)
    (a, b, c) = Fround(c, a, b, Wi, 7)

    Wi = keySchedule(Wi)
    (a, b, c) = Fround(b, c, a, Wi, 9)

    return (a,b,c)

def tigerHash(X):
    # initial values a, b and c
    a = 0x0123456789ABCDEF
    b = 0xFEDCBA9876543210
    c = 0xF096A5B4C3B2E187

    for chunk in X:
        (a,b,c) = outerRound(chunk, a, b, c)
    return (a, b, c) 

def main():

    s = input()

    bytesArray = bytearray()
    bytesArray.extend(map(ord, s))

    X = chunkInput(bytesArray)

    (a, b, c) = tigerHash(X)

    # print in hex, should be binary.
    print("%016X%016X%016X" % (a, b, c))
 
main()
