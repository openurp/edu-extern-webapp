[#ftl]
[@b.head/]
<table  class="gridtable">
 <tr>
    <th class="gridselect-top">学院</th>
    <th>总人数</th>
    [#list subjects as subject]
    <th>${subject.name}通过数</th>
    [/#list]
 </tr>
[#list departMap?keys as departid]
 <tr class="[#if departid_index%2==0]griddata-even[#else]gridselect[/#if]">
    <td>${departMap[departid][0].name}</td>
    <td>${departMap[departid][1]}</td>
    [#list subjects as subject]
    <td>${(gradeMap[departid].getItem(subject).countors[0])!}</td>
    [/#list]
 </tr>
[/#list]
</table>
[@b.foot/]
