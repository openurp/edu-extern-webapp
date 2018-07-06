[#ftl]
[@b.head/]
[@b.toolbar title="校外考试成绩单"]
      bar.addPrint();
      bar.addBackOrClose();
[/@]
<DIV align="center" id="dayinDiv" name="dayinDiv">
  <table id="bar"></table>
</DIV>
<h2 align="center">校外考试成绩单</h2>
 [@b.grid items=examGrades var="examGrade"]
      [@b.row]
            [@b.col title="序号"]${examGrade_index+1}[/@]
            [@b.col property="std.user.code" title="std.user.code"]
                <div style="padding-left:15px;text-align:left">
                    [@b.a href="studentSearch!info?student.id="+examGrade.std.id title="${b.text('info.user.std')}" ]${(examGrade.std.user.code)!}[/@]
                </div>
           [/@]
            [@b.col property="std.name" title="姓名"/]
            [@b.col property="semester.id" title="学期"]${(examGrade.semester.schoolYear)!} &nbsp;${(examGrade.semester.name)!}[/@]
            [@b.col property="subject.name" title="考试科目"/]
            [@b.col property="score" title="成绩"/]
            [#--][@b.col property="examNo" title="准考证号"/][--]
            [@b.col property="certificateNo" title="证书编号"/]
        [/@]
[/@]
[@b.foot/]
