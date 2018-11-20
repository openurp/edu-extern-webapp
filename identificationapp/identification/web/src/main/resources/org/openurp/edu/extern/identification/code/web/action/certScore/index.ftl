[#ftl]
[@b.head/]
  [@b.toolbar title="证书认定标准配置管理"/]
  [#include "/component/certificate/selector.ftl"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view" style="white-space: nowrap">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="certScores" theme="search"]
          [#assign examSubjectElement = { "name": "certScore.examSubject.id", "empty": "全部" }/]
          [#assign certTypeElement = { "name": "certScore.certType.id", "empty": "全部" }/]
          [#assign certLevelElement = { "name": "certScore.certLevel.id", "empty": "全部" }/]
          [#assign divisionElement = { "name": "certScore.division.id", "empty": "全国" }/]
          [#assign examTimeElement = { "name": "certScore.examTime.id", "empty": "全部" }/]
          [@certSelects "searchForm" examSubjectElement certTypeElement certLevelElement divisionElement examTimeElement ""/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="certScores"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "certScores");
      });
    });
  </script>
[@b.foot/]
