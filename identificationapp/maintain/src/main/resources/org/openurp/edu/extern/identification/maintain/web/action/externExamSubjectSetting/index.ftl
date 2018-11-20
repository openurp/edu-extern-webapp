[#ftl]
[@b.head/]
  <style>
    option {
      font-family: 宋体;
    }
  </style>
  [@b.toolbar title="证书大类数据来源配置"]
    bar.addItem("字段配置", function() {
      bg.Go("${b.url("extern-exam-subject-field")}", "main");
    }, "update.png");
  [/@]
  [#include "/component/certificate/const.ftl"/]
  ${MUST_BE_INNER_FIELD_COMMENT}
  <table class="indexpanel">
    <tr>
      <td class="index_view" style="width: 180px">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="settings" theme="search"]
          [@b.select label="证书大类" name="setting.examSubject.id" items=examSubjects?sort_by(["code"]) empty="..."/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="settings"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "settings");
      });
    });
  </script>
[@b.foot/]
