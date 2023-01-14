package com.javainuse.config;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.*;

import java.util.Collections;
import java.util.HashMap;

@Configuration
public class TransactionAspect {
    final PlatformTransactionManager transactionManager;

    public TransactionAspect(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Bean
    public TransactionInterceptor transactionAdvice() {
        NameMatchTransactionAttributeSource txAttributeSource = new NameMatchTransactionAttributeSource();
        RuleBasedTransactionAttribute txAttribute = new RuleBasedTransactionAttribute();
        txAttribute.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        txAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        HashMap<String, TransactionAttribute> txMethods = new HashMap<String, TransactionAttribute>();
        txMethods.put("*", txAttribute);
        txAttributeSource.setNameMap(txMethods);

        return new TransactionInterceptor(transactionManager, txAttributeSource);
    }

    @Bean
    public Advisor transactionAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.javainuse..service.*.*(..))");
        return new DefaultPointcutAdvisor(pointcut, transactionAdvice());
    }
}
