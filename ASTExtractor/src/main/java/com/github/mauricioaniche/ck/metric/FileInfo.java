package com.github.mauricioaniche.ck.metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import javafx.util.Pair;

public class FileInfo extends ASTVisitor {
	
	private ArrayList<Pair<String,String>> classNameAndTypeList;
	Pair<String,String> currentMethod;


	@Override
	public boolean visit(TypeDeclaration node) {
		SimpleName timepass = node.getName();
		String name = timepass.getIdentifier();

		String className = getFullClassName(node.resolveBinding());
		if(className != null)
		{
			String type;
			if(node.isInterface()) type = "interface";
			else type = "class";
			Pair<String,String> classNameAndType = new Pair<String,String>(className,type);
			if(classNameAndTypeList == null)
				classNameAndTypeList = new ArrayList<Pair<String,String>>();
			classNameAndTypeList.add(classNameAndType);
		}
		return true;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		String className = getFullClassName(node.resolveBinding());
		SimpleName timepass = node.getName();
		String name = (String) timepass.getProperty("IDENTIFIER");
		if(className != null)
		{
			String type = "enum";
			Pair<String,String> classNameAndType = new Pair<String,String>(className,type);
			if(classNameAndTypeList == null)
				classNameAndTypeList = new ArrayList<Pair<String,String>>();

			classNameAndTypeList.add(classNameAndType);
		}
		return true;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		SimpleName timepass = node.getName();
		String name = (String) timepass.getProperty("IDENTIFIER");
		String methodName = getFullMethodName(node.resolveBinding());
		String className = getFullClassName(node.resolveBinding());
		if(methodName != null && className != null)
		{
			currentMethod = new Pair<String,String>(methodName,className);
			if(methodName.equals("main"))
			{
				mainMethodAndClassPair = currentMethod;
			}
		return true;
		}
		return false;
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {
		currentMethod = null;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		SimpleName timepass = node.getName();
		String name = (String) timepass.getProperty("IDENTIFIER");
		String methodName = getFullMethodName(node.resolveMethodBinding());
		String className = getFullClassName(node.resolveMethodBinding());
		if(methodName != null && className != null)
		{
			Pair<String,String> invokedMethod; 
			invokedMethod = new Pair<String,String>(methodName,className);
			addMethodInvocation(currentMethod,invokedMethod);
		}		
		return false;
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		
		String methodName = getFullMethodName(node.resolveConstructorBinding());
		String className = getFullClassName(node.resolveConstructorBinding());
		if(methodName != null && className != null)
		{
			Pair<String,String> invokedMethod; 
			invokedMethod = new Pair<String,String>(methodName,className);
			addMethodInvocation(currentMethod,invokedMethod);
		}		
		return false;
	}

	private static String getFullClassName(ITypeBinding binding) {
		if(binding!=null)
			return binding.getBinaryName();
		return null;
	}
	
	public static String getFullMethodName(IMethodBinding binding) {
		if(binding!=null)
			return binding.getName();
		return null;
	}
	
	public static String getFullClassName(IMethodBinding binding) {
		if(binding!=null)
			return getFullClassName(binding.getDeclaringClass());
		return null;
	}

	
	public ArrayList<Pair <String,String>> methodReachableFromMain;
	public Map<Pair<String,String>,ArrayList<Pair<String,String>>> methodInvocationGraph;
	
	public Pair<String,String> mainMethodAndClassPair;
	
	private void addMethodInvocation(Pair<String,String> invokingMethod, Pair<String,String> invokedMethod)
	{
		if(methodInvocationGraph == null)
			methodInvocationGraph = new HashMap<Pair<String,String>,ArrayList<Pair<String,String>>>();
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
	}
	
	public void updateMethodReachableFromMain()
	{
		methodReachableFromMain = new ArrayList<Pair<String,String>>();
		updateMethodReachableFromMain(mainMethodAndClassPair);
	}
	
	private void updateMethodReachableFromMain(Pair<String,String> invokingMethod)
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
