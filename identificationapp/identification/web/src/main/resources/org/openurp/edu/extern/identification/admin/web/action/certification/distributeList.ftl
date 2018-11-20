[#ftl]
[@b.head/]
  [@b.grid id="certificationCourse" items=courses var="course" sortable="false"]
    [@b.gridbar]
      bg.form.addInput(action.getForm(), "certification.id", "${Parameters["certification.id"]}");
      bar.addItem("添加", function() {
        bg.form.submit(action.getForm(), "${b.url("!toDistribute")}");
      }, "${b.theme.iconurl("actions/new.png")}");
      bar.addItem("删除",action.remove());
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="课程代码" property="course.code"/]
      [@b.col title="课程名称" property="course.name"/]
      [@b.col title="得分" property="score"/]
      [@b.col title="显示" property="scoreValue"/]
    [/@]
  [/@]
[@b.foot/]
