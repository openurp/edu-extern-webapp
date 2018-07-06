<#include "/template/head.ftl"/>
 <#include "sum.ftl"/>
<body >
  <table id="examSumBar" width="100%"></table>
  <@table.table  id="listTable" sortable="true" width="100%">
    <@table.thead>
      <td>班级</td>
      <td>${category.name}</td>
    </@>
    <@table.tbody datas=sumList;sum>
      <td>${sum[0]}</td>
      <td>${sum[1]}</td>
    </@>
   </@>
    <form name="actionForm" method="post" action="" >
        <input type="hidden" id="key" name="key" value="" />
    </form>
    <script>
     var bar=new ToolBar('examSumBar','校外考试报名汇总',null,true,true);
     bar.setMessage('<@getMessage/>');


    </script>
</body>
<#include "/template/foot.ftl"/>
