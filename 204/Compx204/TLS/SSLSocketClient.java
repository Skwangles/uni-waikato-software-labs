


class SSLSocketClient
{
	public void main(String args[])
	{
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket)factory.createSocket(<host>, <port>);
		socket.startHandshake();
		/*
		* at this point, can getInputStream and
		* getOutputStream as you would a regular Socket
		*/

		/*
		* set HTTPS-style checking of HostName
		* before the handshake commences
		*/
		SSLParameters params = new SSLParameters();
		params.setEndpointIdentificationAlgorithm("HTTPS");
		socket.setSSLParameters(params);
		socket.startHandshake();

		socket.startHandshake();
		/* get the X509Certificate for this session */
		SSLSession sesh = socket.getSession();
		X509Certificate cert = (X509Certificate)
		sesh.getPeerCertificates()[0];
		/* extract the CommonName, and then compare */
		getCommonName(cert);
	}	
	
	
	
	
	public String getCommonName(X509Certificate cert)
	{
		String name =	cert.getSubjectX500Principal().getName();
		LdapName ln = new LdapName(name);
		String cn = null;
		for(Rdn rdn : ln.getRdns())
		if("CN".equalsIgnoreCase(rdn.getType()))
		cn = rdn.getValue().toString();
		return cn;
	}
}



