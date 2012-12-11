package info.guardianproject.onionkit.trust;


import info.guardianproject.onionkit.proxy.SocksProxyClientConnOperator;
import android.content.Context;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.ClientConnectionOperator;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.SingleClientConnManager;

public class StrongHttpsClient extends DefaultHttpClient {

  final Context context;
  private HttpHost socksProxy;
  
  public StrongHttpsClient(Context context) {
    this.context = context;
  }

  @Override protected ClientConnectionManager createClientConnectionManager() {
    SchemeRegistry registry = new SchemeRegistry();
    registry.register(
        new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    try {
		registry.register(new Scheme("https", new StrongSSLSocketFactory(context), 443));
    } catch (Exception e) {
        throw new AssertionError(e);
      }
    
    socksProxy = (HttpHost)getParams().getParameter("SOCKS");
    
    if (socksProxy == null)
    {
    	return  new SingleClientConnManager(getParams(), registry);
    }
    else
    {
    	
    
    return new SingleClientConnManager(getParams(), registry)
    		{

				@Override
				protected ClientConnectionOperator createConnectionOperator(
						SchemeRegistry schreg) {
					
					return new SocksProxyClientConnOperator(schreg, socksProxy.getHostName(), socksProxy.getPort());
				}
    	
    		};
    }
  }

}