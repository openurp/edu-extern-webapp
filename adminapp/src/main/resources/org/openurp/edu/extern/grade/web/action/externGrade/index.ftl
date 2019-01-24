[#ftl]
[@b.head/]
  [@b.toolbar title="外校成绩管理"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="externGrades" theme="search"]
          [@b.textfields names="externGrade.std.user.code;学号,externGrade.std.user.name;姓名"/]
          [@b.select label="校外学校" name="externGrade.school.id" items=schools empty="..."/]
          [@b.select label="培养层次" name="externGrade.level.id" items=levels empty="..."/]
          [@b.select label="教育类别" name="externGrade.category.id" items=eduCategories empty="..."/]
          [@b.textfields names="externGrade.majorName;外校专业,externGrade.courseName;外校课程,externGrade.credits;外校学分"/]
          [@b.datepicker id="fromAt" label="录入起时" name="fromAt" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'toAt\\')}"/]
          [@b.datepicker id="toAt" label="录入止时" name="toAt" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'fromAt\\')}"/]
          [@b.select label="是否认定" name="hasCourseGrades" items={ "1": "是", "0": "否" } empty="..."/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="externGrades"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "externGrades");
      });
    });
  </script>
[@b.foot/]
