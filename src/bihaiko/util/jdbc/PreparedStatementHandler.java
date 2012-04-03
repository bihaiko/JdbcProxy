package bihaiko.util.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreparedStatementHandler implements InvocationHandler {

	private final Map<Integer, Object> _args = new HashMap<Integer, Object>();
	private final PreparedStatement _skeleton;
	private final Logger _logger;
	private final String _sql[];

	public PreparedStatementHandler(PreparedStatement ps, Logger logger, String sql) {
		_skeleton = ps;
		_logger = logger;
		sql = sql.replace("?", "TOSPLIT");
		_sql = sql.split("TOSPLIT");
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();
		if(name.startsWith("set")){
			if(args.length==2) {
				_args.put((Integer) args[0], args[1]);
				return method.invoke(_skeleton, args);
			}
		}
		
		if(name.contains("execute"))
			logSql(name);
		
		return method.invoke(_skeleton, args);
	}

	private void logSql(String name) {
		
		StringBuilder buf = new StringBuilder();
		int i = 1;
		for (String sql : _sql) {
			buf.append(sql).append(_args.get(i));
			i++;
		}
		
		_logger.log(Level.INFO, "PreparedStatement." + name + "('" +buf.toString()+ "')");
	}
}
