[#ftl]
[@b.head/]
  [@b.toolbar title="证书大类数据来源配置－字段配置"]
    bar.addItem("返回", function() {
      bg.Go("${b.url("!search")}", "fields");
    }, "backward.png");
  [/@]
  [@b.form name="fieldForm" action="!save" target="fields" theme="list"]
    [#assign elementSTYLE = "width: 200px"/]
    [#assign comment]（系统内部使用的字段；<span style="color:red">红色</span>代表请求必配字段，<span style="color:blue">蓝色</span>代表反馈保存必配字段）[/#assign]
    [@b.textfield label="对外字段" name="field.outerField" value=(field.outerField)! required="true" maxlength="30" style=elementSTYLE/]
    [@b.select label="对内字段" name="field.innerField" items=innerFieldMap value=(field.innerField)! empty="..." required="true" style=elementSTYLE comment=comment/]
    [@b.textfield label="标题" name="field.name" value=(field.name)! required="true" maxlength="30" style=elementSTYLE/]
    [@b.validity]
      var form = document.fieldForm;

      function check() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": form["field.id"].value,
            "outerField": form["field.outerField"].value,
            "innerField": form["field.innerField"].value,
            "name": form["field.name"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }

      $("[name='field.outerField']", document.fieldForm).require().assert(function() {
        return !form["field.outerField"].value.match(/\s/mg);
      }, "不能有空白字符！！！").assert(function() {
        return check();
      }, "该配置已存在！！！");

      $("[name='field.innerField']", document.fieldForm).require().assert(function() {
        return check();
      }, "该配置已存在！！！");

      $("[name='field.name']", document.fieldForm).require().assert(function() {
        return check();
      }, "该配置已存在！！！");
    [/@]
    [@b.formfoot]
      <input type="hidden" name="field.id" value="${(field.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
  <script>
    $(function() {
      $(document).ready(function() {
        $("[name=fieldForm]").find("[name='field.innerField']").children().each(function() {
          [#list fixedRequestFields as fixedField]if ("${fixedField}" == this.value) {
            this.style.color = "red";
          } else [/#list][#list fixedResponseFields as fixedField]if ("${fixedField}" == this.value) {
            this.style.color = "blue";
          }[#if fixedField_has_next] else [/#if][/#list]
        });
      });
    });
  </script>
[@b.foot/]
