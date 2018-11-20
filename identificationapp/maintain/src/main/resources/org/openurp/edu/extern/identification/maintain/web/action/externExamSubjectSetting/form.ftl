[#ftl]
[@b.head/]
  <style>
    input[type=checkbox] {
      vertical-align: middle;
    }
  </style>
  [#include "/component/certificate/const.ftl"/]
  [@b.toolbar title="证书大类数据来源配置维护"]
    bar.addItem("返回", function() {
      bg.form.submit(document.searchForm, "${b.url("!search")}", "settings");
    }, "backward.png");
  [/@]
  [@b.form name="settingForm" action="!save" target="settings" theme="list"]
    [@b.select label="证书大类" name="setting.examSubject.id" items=examSubjects?sort_by(["code"]) required="true" value=(setting.examSubject.id)! style="width:200px"/]
    [@b.textfield label="数据来源URL" name="setting.url" value=(setting.url)! required="true" maxlength="280" style="width:500px"/]
    [#assign comment][#if (fields?size)?default(0) == 0]<span style="color: red">（字段未配置，请点击页面右上角的“字段配置”进行配置）</span>[/#if][/#assign]
    [@b.checkboxes label="请求字段" name="requestFieldIds" items=(fields?sort_by(["name"]))?default([]) valueName="label" required="true" value=(setting.requestFields)! comment=comment/]
    [@b.checkboxes label="反馈字段" name="responseFieldIds" items=(fields?sort_by(["name"]))?default([]) valueName="label" required="true" value=(setting.responseFields)! comment=comment/]
    [@b.datepicker id="beginOn" label="启用日期" name="setting.beginOn" value=(setting.beginOn?string('yyyy-MM-dd'))?default('') required="true" style="width:200px" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="截止日期" name="setting.endOn" value=(setting.endOn?string('yyyy-MM-dd'))?default('') style="width:200px" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="setting.id" value="${(setting.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
  <script>
    $(function() {
      var outerFieldMap = {};
      [#list fields as field]
      outerFieldMap["${field.id}"] = "${field.outerField}";
      [/#list]

      function loadClick(name) {
        $("[name=" + name + "]").click(function() {
          var currIndex = $(this).index();
          var currValue = this.value;
          var aa = $("[name=" + name + "]:checked").each(function() {
            [#--
            console.log("index: " + currIndex + "/" + $(this).index());
            console.log("value: " + currValue + "/" + this.value);
            console.log(outerFieldMap[this.value] == outerFieldMap[currValue] && $(this).index() != currIndex);
            --]
            if (outerFieldMap[this.value] == outerFieldMap[currValue] && $(this).index() != currIndex) {
              this.checked = false;
              return false;
            }
          });
        });
      }

      function settingMustBe(name, value, color) {
        var childObj = $("[name=" + name + "][value=" + value + "]");
        childObj.next().css("color", color);

        var spanObj = $("<span>");
        spanObj.text("√");
        spanObj.css("color", color);
        spanObj.insertBefore(childObj);

        childObj.remove();
      }

      $(document).ready(function() {
        var form = document.settingForm;

        loadClick("requestFieldIds");
        loadClick("responseFieldIds");

        [#list fields as field][#if fixedRequestFields?seq_contains(field.innerField)]settingMustBe("requestFieldIds", "${field.id}", "red");[/#if][/#list]
        [#list fields as field][#if fixedResponseFields?seq_contains(field.innerField)]settingMustBe("responseFieldIds", "${field.id}", "blue");[/#if][/#list]
      });
    });
  </script>
[@b.foot/]
