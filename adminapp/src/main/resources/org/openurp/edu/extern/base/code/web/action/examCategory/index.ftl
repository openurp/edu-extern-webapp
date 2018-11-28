[#ftl]
[@b.head/]
  [@b.toolbar title="校外科目大类管理"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="examCategories" theme="search"]
          [@b.textfields names="examCategory.code;代码,examCategory.name;名称"/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="examCategories"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "examCategories");
      });
    });
  </script>
[@b.foot/]
