package myhibernate.interceptor;

import net.sf.cglib.proxy.Enhancer;

public class ProxyFactory
{
   private static ProxyMethodInterceptor pmi = null;
   static
   {
      pmi = new ProxyMethodInterceptor();
   }
   
   @SuppressWarnings("unchecked")
   public static <T> T newInstance(Class<T> clazz,Class<? extends Interceptable> pmiClazz, int id)
   {
      try
      {
         Object x = clazz.newInstance();
         ProxyMethodInterceptor pmi = new ProxyMethodInterceptor();
         pmi.setTarget(x,pmiClazz,id);
         return (T)Enhancer.create(clazz,pmi);
      }
      catch(Exception e)
      {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }
}
