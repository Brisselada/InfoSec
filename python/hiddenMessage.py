message = "Yes, the alchemists worshipped the antimatter, vowing dark, agile actions while announcing algebra..."


words = message.split(" ")

res = []
for word in words:
    res.append(word[2])

print("".join(res))