[#ftl]
[@b.head/]
  [@b.toolbar title="学生证书申请及分配管理"/]
  [#include "/component/certificate/selector.ftl"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view" style="white-space: nowrap">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="certifications" theme="search"]
          [#assign STYLE = "width: 100px"/]
          [@b.textfields names="certification.std.code;学号,certification.std.name;姓名,certification.std.state.grade;年级" style=STYLE/]
          [#assign examSubjectElement = { "name": "certification.certificate.examSubject.id", "empty": "全部" }/]
          [#assign certTypeElement = { "name": "certification.certificate.certType.id", "empty": "全部" }/]
          [#assign certLevelElement = { "name": "certification.certificate.certLevel.id", "empty": "全部" }/]
          [#assign divisionElement = { "name": "certification.certificate.division.id", "empty": "全国" }/]
          [#assign examTimeElement = { "name": "certification.certificate.examTime.id", "empty": "全部" }/]
          [@certSelects "searchForm" examSubjectElement certTypeElement certLevelElement divisionElement examTimeElement STYLE/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="certifications"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "certifications");
      });
    });
  </script>
[@b.foot/]
