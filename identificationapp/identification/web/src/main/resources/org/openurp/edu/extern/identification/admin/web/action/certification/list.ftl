[#ftl]
[@b.head/]
  [@b.grid id="certification" items=certifications var="certification"]
    [@b.gridbar]
      bar.addItem("申请", function() {
        bg.form.submit(action.getForm(), "${b.url("!apply")}");
      }, "new.png");
      bar.addItem("分配", action.single("distributeIndex"), "${b.theme.iconurl("actions/update.png")}");
      bar.addItem("批准入库", action.single("distributeActivate"), "${b.theme.iconurl("actions/activate.png")}");
      bar.addItem("${b.text("action.info")}", action.info());
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.code" width="110px"/]
      [@b.col title="姓名" property="std.name" width="150px"/]
      [@b.col title="年级" property="std.state.grade" width="50px"/]
      [@b.col title="证书代码" property="certificate.code" width="110px"/]
      [@b.col title="证书名称" property="certificate.name"]<a href="javascript:void(0)">${certification.certificate.name}</a>[/@]
      [@b.col title="报考省份" property="certificate.division.code" width="65px"]${(certification.certificate.division.code[0..1] + "-" + certification.certificate.division.name)!"全国"}[/@]
      [@b.col title="操作人" property="happenBy" width="50px"]${certification.happenBy}[/@]
      [@b.col title="发生时间" property="happenAt" width="90px"]${certification.happenAt?string("yyyy-MM-dd")}[/@]
      [@b.col title="分配否" sortabled="false" width="50px"][#if certification.courses?size == 0]<span style="color: red">否</span>[#else]<span style="color: green">是</span>[/#if][/@]
      [@b.col title="入库否" sortabled="false"][#if (certification.grade.id)?exists]<span style="color: green">由${certification.lastBy}于${certification.lastAt?string("yyyy-MM-dd HH:mm:ss")}批准</span>[#else]<span style="color: red">否</span>[/#if][/@]
    [/@]
  [/@]
  <script>
    $(function() {
      $(document).ready(function() {
        $("#certification a").click(function() {
          var form = action.getForm();
          bg.form.addInput(form, "certification.id", $(this).parent().parent().find(":checkbox").val(), "hidden");
          bg.form.submit(form, "${b.url("!info")}", "certifications");
        });
      });
    });
  </script>
[@b.foot/]
