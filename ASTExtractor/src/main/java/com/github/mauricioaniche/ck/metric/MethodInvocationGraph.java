package com.github.mauricioaniche.ck.metric;

import java.util.ArrayList;
import java.util.Map;

import javafx.util.Pair; 

public class MethodInvocationGraph {
	
	static ArrayList<Pair <String,String>> methodReachableFromMain;
	static Map<Pair<String,String>,ArrayList<Pair<String,String>>> methodInvocationGraph;
	
	static Pair<String,String> mainMethodAndClassPair;
	
	static void addMethodInvocation(Pair<String,String> invokingMethod, Pair<String,String> invokedMethod)
	{
		if(methodInvocationGraph.containsKey(invokingMethod))
		{
			ArrayList<Pair<String,String>> invokedMethodsList = methodInvocationGraph.get(invokingMethod);
			invokedMethodsList.add(invokedMethod);
			methodInvocationGraph.put(invokingMethod, invokedMethodsList);
		}
		else
		{
			ArrayList<Pair<String,String>> invokedMethodsList = new ArrayList<Pair<String,String>>();
			invokedMethodsList.add(invokedMethod);
			methodInvocationGraph.put(invokingMethod, invokedMethodsList);
		}
		if(invokingMethod.getKey() == "main")
		{
			mainMethodAndClassPair = invokingMethod;
		}
	}
	
	static void updateMethodReachableFromMain()
	{
		updateMethodReachableFromMain(mainMethodAndClassPair);
	}
	
	static void updateMethodReachableFromMain(Pair<String,String> invokingMethod)
	{
		for(Pair<String,String> invokedMethod : methodInvocationGraph.get(invokingMethod))
		{
			if(!methodReachableFromMain.contains(invokedMethod))
			{
				methodReachableFromMain.add(invokedMethod);
				updateMethodReachableFromMain(invokingMethod);
			}
		}
	}
	

}
