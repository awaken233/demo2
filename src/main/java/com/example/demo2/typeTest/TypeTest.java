package com.example.demo2.typeTest;

import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author wlei3
 * @since 2022/10/13 20:30
 */
public class TypeTest {

    private static Class<? extends Message> getMessageClass(MessageHandler handler) {
        // 获得 Bean 对应的 Class 类名。因为有可能被 AOP 代理过。
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(handler);

        // 获取泛型参数类型 AuthRequest
        Type[] interfaces = targetClass.getGenericInterfaces();
        // 获取父类
        Class<?> superclass = targetClass.getSuperclass();
        while (interfaces.length == 0 && superclass != null) {
            interfaces = superclass.getGenericInterfaces();
            superclass = targetClass.getSuperclass();
        }


        if (Objects.nonNull(interfaces)) {
            // 遍历 interfaces 数组
            for (Type type : interfaces) {
                // 要求 type 是泛型参数
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    // 要求是 MessageHandler 接口
                    if (Objects.equals(parameterizedType.getRawType(), MessageHandler.class)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        // 取首个元素
                        if (Objects.nonNull(actualTypeArguments)
                            && actualTypeArguments.length > 0) {
                            return (Class<Message>) actualTypeArguments[0];
                        } else {
                            throw new IllegalStateException(
                                String.format("类型(%s) 获得不到消息类型", handler));
                        }
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
    }

    public static void main(String[] args) {
        Class<? extends Message> messageClass = getMessageClass(new AuthMessageHandler());
        System.out.println(messageClass);
    }
}
