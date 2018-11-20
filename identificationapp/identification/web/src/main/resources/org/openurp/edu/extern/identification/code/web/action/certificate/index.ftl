[#ftl]
[@b.head/]
  <style>
    option {
      font-family: 宋体;
    }
  </style>
  [@b.toolbar title="开放的证书（配置）管理"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view" style="white-space: nowrap">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="certificates" theme="search"]
          [@b.textfields names="certificate.code;代码,certificate.name;名称"/]
          [#--
          [@b.textfields names="certificate.std.user.code;学号,certificate.std.user.name;姓名,certificate.std.state.grade;年级"/]
          [@b.select label="院系" name="certificate.std.state.department.id" items=departments?sort_by(["name"]) empty="..."/]
          --]
          [@b.select label="大类" name="certificate.type.examSubject.id" items=subjects?sort_by(["code"]) empty="..."/]
          [@b.select label="科目/子类" name="certificate.type.id" items=types empty="..."/]
          [@b.select label="级别" name="certificate.level.id" items=levels empty="..."/]
          [@b.select label="省份" name="certificate.division.id" items=divisions?sort_by(["code"]) option=r"${item.code[0..1] + '-' + item.name }" empty="全国"/]
          [@b.select label="报考时间" name="certificate.examTime.id" items=times?sort_by(["code"]) empty="..."/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="certificates"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "certificates");
      });
    });
  </script>
[@b.foot/]
