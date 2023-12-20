package org.processmining.congliu.PreprocessingCSVLog;

/**
 * this class contains all need attributes in the final xes log.
 * @author cliu3
 *
 */
public class AttributeUnit {
	
	private String caseId = null;//
	private String methodname = null;// 
	private String classiIdentifier = null;//
	private String packageIdentifier = null;//
	private String parameterType = null;//
	private String returnType = null;//
	private String paramterValue = null;//
	private String returnValue = null;//
	private String methodType = null;//{constructor, method call}
	private String runtimeModule = null;
	private String BelongingModule = null;
	private String methodAttributed = null; //{internal, intra, inter}
	private String startTime = null;//
	private String endTime = null;//
	
	//serialize the attributes
	
	public String serialize()
	{
		StringBuilder strB = new StringBuilder();
		strB.append(caseId);
		strB.append(",");
		strB.append(methodname);
		strB.append(",");
		strB.append(classiIdentifier);
		strB.append(",");
		strB.append(packageIdentifier);
		strB.append(",");
		strB.append(runtimeModule);
		strB.append(",");
		strB.append(BelongingModule);
		strB.append(",");
		strB.append(methodAttributed);
		strB.append(",");
		strB.append(methodType);
		strB.append(",");
		strB.append(startTime);
		strB.append(",");
		strB.append(endTime);
		strB.append(",");
		strB.append(parameterType);
		strB.append(",");
		strB.append(returnType);
		strB.append(",");
		strB.append(paramterValue);
		strB.append(",");
		strB.append(returnValue);
		
		return strB.toString();
		
	}
	
	//source-->generate getters and setters
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getMethodname() {
		return methodname;
	}
	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}
	public String getClassiIdentifier() {
		return classiIdentifier;
	}
	public void setClassiIdentifier(String classiIdentifier) {
		this.classiIdentifier = classiIdentifier;
	}
	public String getPackageIdentifier() {
		return packageIdentifier;
	}
	public void setPackageIdentifier(String packageIdentifier) {
		this.packageIdentifier = packageIdentifier;
	}
	public String getParameterType() {
		return parameterType;
	}
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getParamterValue() {
		return paramterValue;
	}
	public void setParamterValue(String paramterValue) {
		this.paramterValue = paramterValue;
	}
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	public String getMethodType() {
		return methodType;
	}
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	public String getRuntimeModule() {
		return runtimeModule;
	}
	public void setRuntimeModule(String runtimeModule) {
		this.runtimeModule = runtimeModule;
	}
	public String getBelongingModule() {
		return BelongingModule;
	}
	public void setBelongingModule(String belongingModule) {
		BelongingModule = belongingModule;
	}
	public String getMethodAttributed() {
		return methodAttributed;
	}
	public void setMethodAttributed(String methodAttributed) {
		this.methodAttributed = methodAttributed;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
