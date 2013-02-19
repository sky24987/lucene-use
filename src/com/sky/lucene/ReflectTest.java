package com.sky.lucene;

import java.lang.reflect.Method;

public class ReflectTest {
	 /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //ʵ����һ������
        MethodTest methodTest = new MethodTest();
        
        Method[] declaredMethods = MethodTest.class.getDeclaredMethods();  //������еķ���
        for(int i=0;i<declaredMethods.length;i++)
        {
            Method method = declaredMethods[i];
            System.out.println("���ƣ�"+method.getName());   //��������
            System.out.println("�Ƿ�������пɱ��������:"+method.isVarArgs());//
            System.out.println("��ڲ�����������Ϊ��");
            Class[]parameterTypes = method.getParameterTypes();//��÷������еĲ�������
            for(int j=0;j<parameterTypes.length;j++)
            {
                System.out.println("parameterTypes[" + j + "]" + parameterTypes[j]);
            }
            System.out.println("����ֵ���ͣ�"+method.getReturnType()); //��÷�������ֵ����
            System.out.println("�����׳��쳣�����У�");
            Class []exceptionTypes = method.getExceptionTypes();  //��ÿ����׳��������쳣����
            for(int j=0;j<exceptionTypes.length;j++){
                System.out.println("exceptionTypes[" + j + "]" + exceptionTypes[j]);
            }
            boolean isTurn = true;
            while(isTurn)     //�������еķ���
            {
                try
                {
                    isTurn = false;
                    if(i==0)   //��ע�����ɵ�˳�򣬿����Ȱ���Щ���ע�͵�������˳��������ִ�С�
                    {
                        method.invoke(methodTest);
                    }    else if(i==1)
                    {
                        System.out.println("����ֵ��" + method.invoke(methodTest,168));
                    }else if(i==2)
                    {
                        System.out.println("����ֵ��" + method.invoke(methodTest,"7",5));
                    }else if(i==3)
                    {
                        Object[] parameters = new Object[]{new String[]{"M","W","Q"}};
                        System.out.println("����ֵ��"+method.invoke(methodTest, parameters));
                    }
                }catch(Exception e)
                {
                    System.out.println("��ִ�з���ʱ�׳��쳣,ִ��setAccessible()����");
                    method.setAccessible(true);
                    isTurn = true;
                }
            }
            System.out.println("****************");
        }
    }
}
