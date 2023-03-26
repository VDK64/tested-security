For generate asymmetric keys use OpenSSL:
<ul>
 <li>Generate private key of size 2048 using: openssl genrsa -out private.pem 2048</li>
 <li>Generate public key from private key using: openssl rsa -in private.pem -outform PEM -pubout -out public.pem</li>
</ul>
