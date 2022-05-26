package com.shawn.study.deep.in.spring.core.bean.dependency.injection.setter;

import com.shawn.study.deep.in.spring.core.bean.dependency.injection.domain.UserHolder;
import com.shawn.study.deep.in.spring.core.ioc.domain.User;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

public class AutoWiringByNameDependencySetterInjectionDemo {

  public static void main(String[] args) {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
    reader.loadBeanDefinitions("classpath:/dependency-setter-injection.xml");
    UserHolder userHolder =
        beanFactory.getBean("userHolder-auto-set-user-by-name", UserHolder.class);
    User user = userHolder.getUser();
    System.out.printf("通过xml配置bean，使用setter方法自动注入（byName方式）user: %s\n", user);
  }
}
