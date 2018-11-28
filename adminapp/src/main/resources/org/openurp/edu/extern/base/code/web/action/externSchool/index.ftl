[#ftl]
[@b.head/]
  [@b.toolbar title="外校信息管理"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="externSchools" theme="search"]
          [@b.textfields names="externSchool.code;代码,externSchool.name;名称"/]
          [@b.select label="国家地区" name="externSchool.country.id" items=countries empty="..."/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="externSchools"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "externSchools");
      });
    });
  </script>
[@b.foot/]
