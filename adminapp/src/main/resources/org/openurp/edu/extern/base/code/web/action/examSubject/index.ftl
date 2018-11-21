[#ftl]
[@b.head/]
  [@b.toolbar title="校外考试科目管理"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="examSubjects" theme="search"]
          [@b.textfields names="examSubject.code;代码,examSubject.name;名称"/]
          [@b.select label="考试类型" name="examSubject.category.id" items=examCategories empty="..."/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="examSubjects"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "examSubjects");
      });
    });
  </script>
[@b.foot/]
