package myhibernate.interceptor;

import java.lang.reflect.Method;

public interface Interceptable
{
   public void onMethodCall(Object target, Method method, Object[] args, int id);
}
