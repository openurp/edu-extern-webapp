[#ftl]
[@b.head/]
  [@b.toolbar title="证书配置"]
    bar.addBack();
  [/@]
  [@b.form name="certificateForm" action="!save" target="certificates" theme="list"]
    [#assign STYLE = "width:300px"/]
    [@b.textfield label="代码" name="certificate.code" required="true" maxlength="20" value=(certificate.code)! style=STYLE/]
    [@b.textfield label="名称" name="certificate.name" required="true" maxlength="100" value=(certificate.name)! style=STYLE/]
    [@b.select label="大类" name="examSubjectId" items=subjects?sort_by(["code"]) required="true" value=(certificate.type.examSubject.id)! style=STYLE/]
    [@b.select label="科目/子类" name="certificate.type.id" required="true" value=(certificate.type.id)! style=STYLE/]
    [@b.select label="级别" name="certificate.level.id" items=levels empty="－" value=(certificate.level.id)! style=STYLE/]
    [@b.select label="省份" name="certificate.division.id" items=divisions?sort_by(["code"]) option=r"${item.code[0..1] + '-' + item.name}" empty="全国" value=(certificate.division.id)! style=STYLE/]
    [@b.select label="报考时间" name="certificate.examTime.id" items=times?sort_by(["code"]) required="true" value=(certificate.examTime.id)! style=STYLE/]
    [@b.validity]
      var isOk = true;

      $("[name='certificate.code']", document.certificateForm).assert(function() {
        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkPrimaryAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": document.certificateForm["certificate.id"].value,
            "code": document.certificateForm["certificate.code"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "当前证书代码已存在！！！");
    [/@]
    [@b.datepicker id="beginOn" label="启用日期" name="certificate.beginOn" value=(certificate.beginOn?string('yyyy-MM-dd'))?default('') required="true" style="width:200px" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="截止日期" name="certificate.endOn" value=(certificate.endOn?string('yyyy-MM-dd'))?default('') style="width:200px" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="certificate.id" value="${(certificate.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
  <script>
    $(function() {
      $(document).ready(function() {
        var form = document.certificateForm;

        var examSubject = form["examSubjectId"];
        var type = form["certificate.type.id"];

        $(examSubject).change(function() {
          $(type).empty();
          $(type).css("color", "");

          $.ajax({
            "type": "POST",
            "url": "${b.url("!loadCertTypesAjax")}",
            "async": false,
            "dataType": "html",
            "data": {
              "id": $(this).val()
            },
            "success": function(data) {
              $(type).append(data);
              $(type).change();
              if (!$(type).val()) {
                $(type).css("color", "red");
              }
            }
          });
        });

        $(examSubject).change();
      });
    });
  </script>
[@b.foot/]
