[#ftl]
[@b.head/]
  [@b.toolbar title = "<span style=\"color:blue\">" + certification.std.user.name + "（<span style=\"padding-left: 1px; padding-right: 1px\">" + certification.std.user.code + "</span>）</span>证书课程及成绩分配"]
    bar.addItem("返回", function() {
      bg.form.submit(document.searchForm);
    }, "backward.png");
  [/@]
  <table class="infoTable" style="width: auto;background-color: white">
    <tr>
      <td class="title">证书名称：</td>
      <td>${certification.certificate.name}</td>
      <td class="title">证书编号：</td>
      <td>${certification.code}</td>
      <td class="title">证书成绩：</td>
      <td>${certification.score}</td>
    </tr>
  </table>
  [@b.div id="coursesDiv"/]
  [@b.form name="distributeListForm" action="!distributeList" target="coursesDiv"]
    <input type="hidden" name="certification.id" value="${certification.id}">
  [/@]
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.distributeListForm, "${b.url("!distributeList")}", "coursesDiv");
      });
    });
  </script>
[@b.foot/]
