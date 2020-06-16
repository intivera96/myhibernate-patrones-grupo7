package myhibernate.interceptor;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyMethodInterceptor implements MethodInterceptor
{
	private Object target;
	private Class<?> interceptorClazz;
	private int id;

	public void setTarget(Object target,Class<? extends Interceptable> interceptorClazz, int id)
	{
	   this.target = target;
	   this.interceptorClazz = interceptorClazz;
	   this.id = id;
	   
	}
	
   @Override
   public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
   {
      try
      {
         Interceptable interceptor = (Interceptable)interceptorClazz.newInstance();
         interceptor.onMethodCall(target,method,args, id);
         return proxy.invoke(target,args);         
      }
      catch(Exception e)
      {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }
}

