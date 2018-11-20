[#ftl]
[@b.head/]
  [@b.div id="coursesAjax" style="height: 310px; overflow-x: hidden; overflow-y: auto"]
    [@b.form name="courseAjaxForm" action="!coursesAjax" target="coursesAjax"]
      <input type="hidden" name="courseIds" value="${Parameters["courseIds"]}"/>
      [@b.grid items=courses var="course" filterable="true"]
        [@b.gridbar]
          bar.addItem("${b.text("action.add")}", function() {
            var ids_ = bg.input.getCheckBoxValues("course.id");
            if (null == ids_ || ids_.replace(/s/mg, "").length == 0) {
              alert("请至少选择一门确认需要添加的可替课程，谢谢。");
              return;
            }

            var dataParamMap = {};
            var tbodyObj = btnAddCourse.parent().parent().parent().find("table").find("tbody");
            tbodyObj.children().each(function(i, trObj) {
              $(trObj).find("input").each(function(i, inputObj) {
                dataParamMap[$(inputObj).attr("name")] = $(inputObj).val();
              });
            });
            var k = tbodyObj.children().size();
            var ids = ids_.split(",");
            for (var i = 0; i < ids.length; i++) {
              dataParamMap["csCourse" + (k + i) + ".course.id"] = ids[i];
              dataParamMap["csCourse" + (k + i) + ".certScore.id"] = "${Parameters["id"]!}";
            }

            var courses = [];
            $.ajax({
              "type": "POST",
              "url": "${b.url("!loadCourseDataAjax")}",
              "async": false,
              "dataType": "json",
              "data": dataParamMap,
              "success": function(data) {
                tbodyObj.html(data.courseDataRow);
                document.certScoreForm["courseIds"].value = data.courseIds;
                window.csCoursesValidity = function() {
                  eval(data.csCoursesValidity);
                };

                loadBtnRemoveEvent(); [#--补上“删除”事件--]

                $.colorbox.close();
              },
              "error": function(rs, e, errObj) {
                console.log(e);
                console.log(errObj);
              }
            });
          }, "new.png");

          bar.addItem("关闭", function() {
            $.colorbox.close();
          });
        [/@]
        [@b.row]
          [@b.boxcol/]
          [@b.col title="代码" property="code"/]
          [@b.col title="名称" property="name"/]
          [@b.col title="学分" property="credits"/]
        [/@]
      [/@]
    [/@]
  [/@]
[@b.foot/]
