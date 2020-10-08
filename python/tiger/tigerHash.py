import math

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

def iotToBitArray(input):
    s = "{0:b}".format(input)
    res = []
    for c in s:
        res.append(int(c))
    return res   

def chunkInput(array):
    amountOfCunks = math.ceil(len(array) / 64)
    chunks = []
    current = 0
    for x in range(amountOfCunks):
        currentChunk = []
        messageBits = array[current: current + (x + 1) * 64]

        current += 64

        currentChunk.append(1)

        while len(currentChunk) + len(messageBits) < 512:
            currentChunk.append(0)

        currentChunk = currentChunk + messageBits
        chunks.append(currentChunk)
    return chunks

def F5(a, b, c):
    # Do 8 inner rounds
    return (a, b, c)

def F7(a, b, c):
   # Do 8 inner rounds
    return (a, b, c)

def F9(a, b, c):
   # Do 8 inner rounds
    return (a, b, c)


def fm(a,b,c):
    # do stuff
    return (a, b, c)

def innerRound(a, b, c):
    # 8 inner rounds
    for i in range(8):
        (a, b, c) = fm(c, a, b)
    return (a, b, c)

def outerRound(Xi):
    # 64 bits and the initial values a, b and c
    a = iotToBitArray(0x0123456789ABCDEF)
    b = iotToBitArray(0xFEDCBA9876543210)
    c = iotToBitArray(0xF096A5B4C3B2E187)

    (a, b, c) = F5(a, b, c)
    (a, b, c) = F7(c, a, b)
    (a, b, c) = F9(b, c, a)

    return (a,b,c)





inputBits = tobits(input())

X = chunkInput(inputBits)

for chunk in X:
    (a,b,c) = outerRound(chunk)


print(frombits(a + b + c))