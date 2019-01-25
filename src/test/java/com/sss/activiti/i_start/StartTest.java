package com.sss.activiti.i_start;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.util.List;

/**
 *流程实例开始结束
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StartTest {

    @Autowired
    private RepositoryService repositoryService;    //与流程定义和部署对象相关的service

    @Autowired
    private RuntimeService runtimeService;  //与正在执行的流程实例和执行对象相关的service

    @Autowired
    private TaskService taskService;    //与正在执行的任务管理相关的service

    @Autowired
    private HistoryService historyService;  //与历史数据(从历史表)相关的service

    //手动部署流程定义(从InputStream)
    @Test
    public void deploymentProcessDefinition_InputStream() {
        InputStream inputStreamBpmn = this.getClass().getResourceAsStream("start.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("start.png");
        Deployment deploy = repositoryService.createDeployment()//创建一个部署对象
                .name("开始活动")//添加部署的名称
                .addInputStream("start.bpmn", inputStreamBpmn)
                .addInputStream("start.png", inputStreamPng)
                .deploy();//完成部署
        System.out.println("流程部署ID： " + deploy.getId());
        System.out.println("流程部署名称： " + deploy.getName());
    }

    //启动流程实例
    @Test
    public void startProcessInstance() {
        //流程定义的key
        String key = "start";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);//使用流程定义的key启动流程实例，key对应的helloworld文件中的id属性值,使用key启动，默认是按照最新版本的流程定义启动
        System.out.println("流程实例ID： " + processInstance.getId());
        System.out.println("流程定义ID： " + processInstance.getProcessDefinitionId());

        /* 判断流程是否结束,查询正在执行的执行对象表 */
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()//创建流程实例查询对象
                .processInstanceId(processInstance.getId())
                .singleResult();
        //说明流程实例结束了
        if (pi == null) {
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstance.getId())//使用流程实例ID查询
                    .singleResult();
            System.out.println(hpi.getId()+"  "+hpi.getStartTime()+"  "+hpi.getEndTime()+"  "+hpi.getDurationInMillis());
        }

    }



}
