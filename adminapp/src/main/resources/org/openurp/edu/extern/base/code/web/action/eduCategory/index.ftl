[#ftl]
[@b.head/]
  [@b.toolbar title="教育类别管理"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="eduCategories" theme="search"]
          [@b.textfields names="eduCategory.code;代码,eduCategory.name;名称"/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="eduCategories"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "eduCategories");
      });
    });
  </script>
[@b.foot/]
