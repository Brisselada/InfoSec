encrypt = input() == "e"
arguments = input().split(" ")

p = int(arguments[0])
q = int(arguments[1])
e = int(arguments[2])

def egcd(a, b):
    if a == 0:
        return (b, 0, 1)
    else:
        g, y, x = egcd(b % a, a)
        return (g, x - (b // a) * y, y)


phiN = (p - 1) * (q - 1)
N = p * q
g, x, y = egcd(e, phiN)
d = x % phiN
done = False

try:
    while True:
        if encrypt:
            M = int(input())
            C = pow(M, e, N)
            print(C)
        else:
            C = int(input())
            M = pow(C, d, N)
            print(M)
except EOFError as e:
    done = True
