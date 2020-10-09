encrypt = input() == "e"
arguments = input().split(" ")

p = int(arguments[0])
q = int(arguments[1])
e = int(arguments[2])

# perform the extended Euclidean algorithm on value a and b
# returns (gcd(b, n), a, m) such that a*b + n*m = gcd(b, n).
def egcd(a, b):
    if a == 0:
        return (b, 0, 1)
    else:
        g, y, x = egcd(b % a, a)
        return (g, x - (b // a) * y, y)

# Calculate the exponantional result with a given modulo
# using Modular exponentiation
def modularExponentiation(value, exp, modulo):
    if exp == 0:
        return 1 % modulo

    if exp == 1:
        return value % modulo

    # if exp is odd
    if exp % 2 != 0:
        return ((value % modulo) * modularExponentiation(value, exp -1, modulo) % modulo)

    result = modularExponentiation(value, exp / 2, modulo)
    return (result * result) % modulo

# Calulate the modulo and public N
phiN = (p - 1) * (q - 1)
N = p * q

# Get the results from the extended Euclidean algorithm
g, x, y = egcd(e, phiN)
# Calulate the private key with the results
d = x % phiN

# done when an end of file exception is thrown
done = False

try:
    while True:
        if encrypt:
            M = int(input())
            # encrypt using public key 'e' and N
            C = modularExponentiation(M, e, N)
            print(C)
        else:
            C = int(input())
            # dencrypt using private key 'd' and public N
            M = modularExponentiation(C, d, N)
            print(M)
except EOFError as e:
    done = True
