[#ftl]
[@b.head/]
  [@b.toolbar title="考试项目基础数据管理"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="examTimes" theme="search"]
          [@b.textfields names="examTime.code;代码,examTime.name;名称"/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="examTimes"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "examTimes");
      });
    });
  </script>
[@b.foot/]
