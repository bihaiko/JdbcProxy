package bihaiko.util.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatementHandler implements InvocationHandler {

	private final Statement _skeleton;
	private final Logger _logger;
	

	public StatementHandler(Statement statement, Logger logger) {
		_skeleton = statement;
		_logger = logger;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();
		if(name.contains("execute"))
			_logger.log(Level.INFO, "Statement." + name + "('" + args[0] + "')");
		
		return method.invoke(_skeleton, args);
	}
}
