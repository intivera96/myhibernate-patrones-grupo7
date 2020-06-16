package myhibernate.interceptor;

import java.lang.reflect.Method;

import myhibernate.MyHibernate;

public class Interceptor implements Interceptable
{
   @Override
   public void onMethodCall(Object target,Method method,Object[] args, int id)
   {
	   try
		  {
			   if (method.getName().startsWith("get") && (method.invoke(target) == null || method.invoke(target).equals(0)))
			   {
				   Class<?> interceptedClass = target.getClass();
				   
				   target = MyHibernate.find(interceptedClass,id);
				   
				   System.out.println(
						   target.getClass().getMethod("get" + method.getName().substring(3)).invoke(target));
			   }
		  }
		  catch (Exception e)
		  {
				  e.printStackTrace();
		  }
	   }
   }
