[#ftl]
[@b.head/]
[#if projects?size>0]
<div class="navmenu">
<ul>
	[#list projects as project]
	<li><a [#if project.id=defaultProjectId]class="selected" href="javascript:void(0);"[#else] onclick="return bg.Go(this,null)" href="${b.url('!index?projectId=${project.id}')}"[/#if]>${project.name}</a></li>
	[/#list]
</ul>
</div>
[@b.div href="!innerIndex?projectId=${defaultProjectId}"/]
[#else]
没有数据!
[/#if]
[@b.foot/]