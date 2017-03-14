<#macro showSum(signupMap,depts,category)>
  <#assign sum=0 />
 <#list depts as dept>
   <#if signupMap[dept.id+"_"+category.id]?exists>
   <#assign sum=sum+signupMap[dept.id+"_"+category.id]?int>
   </#if>
 </#list>
 <td align="center"><b>${sum}</b></tr>
 </#macro>