[#ftl]
[@b.head/]
  [@b.toolbar title="证书级别基础数据管理"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="levels" theme="search"]
          [@b.textfields names="level.code;代码,level.name;名称"/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="levels"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "levels");
      });
    });
  </script>
[@b.foot/]
