[#ftl]
[@b.head/]
  [@b.toolbar title="外校成绩管理"/]
  <div style="color: blue">注：以下字段除学号、姓名外，都来自这些学生当时在外校中就读的情况。</div>
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="externGrades" theme="search"]
          [@b.textfields names="externGrade.std.user.code;学号,externGrade.std.user.name;姓名"/]
          [@b.select label="校外学校" name="externGrade.school.id" items=schools empty="..."/]
          [@b.select label="培养层次" name="externGrade.level.id" items=levels empty="..."/]
          [@b.select label="教育类别" name="externGrade.category.id" items=eduCategories empty="..."/]
          [@b.textfields names="externGrade.majorName;外校专业,externGrade.courseName;外校课程,externGrade.credits;外校学分"/]
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
