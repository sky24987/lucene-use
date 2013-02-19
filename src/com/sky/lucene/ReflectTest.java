package com.sky.lucene;

import java.lang.reflect.Method;

public class ReflectTest {
	 /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //实例化一个对象：
        MethodTest methodTest = new MethodTest();
        
        Method[] declaredMethods = MethodTest.class.getDeclaredMethods();  //获得所有的方法
        for(int i=0;i<declaredMethods.length;i++)
        {
            Method method = declaredMethods[i];
            System.out.println("名称："+method.getName());   //方法名称
            System.out.println("是否允许带有可变参数变量:"+method.isVarArgs());//
            System.out.println("入口参数类型依次为：");
            Class[]parameterTypes = method.getParameterTypes();//获得方法所有的参数类型
            for(int j=0;j<parameterTypes.length;j++)
            {
                System.out.println("parameterTypes[" + j + "]" + parameterTypes[j]);
            }
            System.out.println("返回值类型："+method.getReturnType()); //获得方法返回值类型
            System.out.println("可能抛出异常类型有：");
            Class []exceptionTypes = method.getExceptionTypes();  //获得可能抛出的所有异常类型
            for(int j=0;j<exceptionTypes.length;j++){
                System.out.println("exceptionTypes[" + j + "]" + exceptionTypes[j]);
            }
            boolean isTurn = true;
            while(isTurn)     //调用类中的方法
            {
                try
                {
                    isTurn = false;
                    if(i==0)   //请注意生成的顺序，可以先把这些语句注释掉，看下顺序再另行执行。
                    {
                        method.invoke(methodTest);
                    }    else if(i==1)
                    {
                        System.out.println("返回值：" + method.invoke(methodTest,168));
                    }else if(i==2)
                    {
                        System.out.println("返回值：" + method.invoke(methodTest,"7",5));
                    }else if(i==3)
                    {
                        Object[] parameters = new Object[]{new String[]{"M","W","Q"}};
                        System.out.println("返回值："+method.invoke(methodTest, parameters));
                    }
                }catch(Exception e)
                {
                    System.out.println("在执行方法时抛出异常,执行setAccessible()方法");
                    method.setAccessible(true);
                    isTurn = true;
                }
            }
            System.out.println("****************");
        }
    }
}
