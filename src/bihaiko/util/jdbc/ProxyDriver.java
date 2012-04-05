package bihaiko.util.jdbc;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class ProxyDriver implements Driver {
	
	private static final String PREFIX = "jdbc:bihaiko:";
	private static Logger _loggerProxy;
	
	private Logger _logger;
	private Driver _targetDriver;
	
	static{
		try {
			_loggerProxy = Logger.getLogger(ProxyDriver.class.getName());
			DriverManager.registerDriver(new ProxyDriver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		if(acceptsURL(url) ){
			if(url.startsWith(PREFIX)){
				url = url.split(PREFIX)[1];
				url = url.split("@")[1];
			}
			return (Connection) Proxy.newProxyInstance(	
					getClass().getClassLoader(), 
					new Class[]{Connection.class}, 
					new ConnectionHandler(_targetDriver, _loggerProxy, url, info)
			);
		}
		throw new SQLException("Invalid URL: " + url);
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)	throws SQLException {
		return _targetDriver.getPropertyInfo(url, info);
	}

	@Override	public int getMajorVersion() {return _targetDriver.getMajorVersion(); }
	@Override	public int getMinorVersion() {return _targetDriver.getMinorVersion();}
	@Override	public boolean jdbcCompliant() {return _targetDriver.jdbcCompliant();}
	
	public Logger getParentLogger()  {
		initializeLoggerIfNecessary();
		return _logger;
	}

	private void initializeLoggerIfNecessary() {
		try {
			if(_logger != null) return;	
			
			_logger = (Logger)_targetDriver.getClass()
								.getMethod("getParentLogger", new Class[0])
								.invoke(_targetDriver, new Object[0]);
			
		} catch (Exception e) {
			_logger = Logger.getLogger(_targetDriver.getClass().getName());
		}
	}

	public Logger getLoggerProxy() {
		initializeLoggerIfNecessary();
		return _loggerProxy;
	}
	
	@Override 
	@SuppressWarnings("unchecked")
	public boolean acceptsURL(String url) throws SQLException {
		if(! url.startsWith(PREFIX)) return false;
		url = url.split(PREFIX)[1];
		
		String args[] = url.split("@");
		
		try {
			if(_targetDriver == null)
				_targetDriver = ((Class<Driver>) Class.forName(args[0])).newInstance();
		} catch (Exception e) {
			throw new SQLException(e);
		}
		
		return _targetDriver.acceptsURL(args[1]);
	}
}