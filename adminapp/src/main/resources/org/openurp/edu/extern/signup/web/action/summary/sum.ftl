<#macro showSum(signUpMap,depts,category)>
  <#assign sum=0 />
 <#list depts as dept>
   <#if signUpMap[dept.id+"_"+category.id]?exists>
   <#assign sum=sum+signUpMap[dept.id+"_"+category.id]?int>
   </#if>
 </#list>
 <td align="center"><b>${sum}</b></tr>
 </#macro>