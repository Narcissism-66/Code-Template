<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {useThemeStore} from "@/stores/ThemeStore.js";
import {userUserStore} from "@/stores/userStore.js";

const router = useRouter()
const route = useRoute()
const themeStore = useThemeStore();
const userStore=userUserStore();

// 基础导航数据：支持二级（仅需设置 icon 名称）
const baseNav = [
  {
    label: '首页',
    icon: 'home',
    route: '/',
    show: true // 始终显示
  },{
    label: '发布',
    icon: 'announce',
    show: userStore.user.identity === 'admin',
    children: [
      {label: '公告', route: '/announce/bulletin', icon: 'bulletin', show: userStore.user.identity === 'admin'},
      {label: '研究', route: '/announce/research', icon: 'research', show: userStore.user.identity === 'admin'},
    ],
  },
  {
    label: 'AI',
    icon: 'AI',
    show: true, // 始终显示
    children: [
      {label: 'AI对话', route: '/AI/chat', icon: 'chat', show: true},
      {label: 'AI助手', route: '/AI/assistant', icon: 'chart', show: false},
      {label: 'AI生成', route: '/AI/generate', icon: 'doc', show: false}, // 只有管理员可见
    ],
  },
  {
    label: '工具',
    icon: 'tools',
    show: true,
    children: [
      {label: '文章', route: '/tools/article', icon: 'article', show: true},
      {label: '应用', route: '/tools/app', icon: 'app', show: true},
    ],
  },
  {
    label: '事务',
    icon: 'affairs',
    show: true,
    children: [
      {label: '请假', route: '/affairs/ask-for-leave', icon: 'leave', show: true},
      {label: '获奖', route: '/affairs/prize', icon: 'prize', show: true},
      {label: '申请', route: '/affairs/apply', icon: 'apply', show: false},
      {label: "分享",route: "/affairs/share",icon: 'share', show: true}
    ],
  },
  {
    label: '人员',
    icon: 'personnel',
    show: true,
    children: [
      {label: '个人空间', route: '/personnel/personal', icon: 'personal', show: true},
      {label: '实验室成员', route: '/personnel/member', icon: 'member', show: true}
    ],
  },
  {
    label: '消息中心',
    icon: 'messages',
    show: true,
    children: [
      {label: '收件箱', route: '/messages/inbox', icon: 'inbox', show: true},
      {label: '发邮件', route: '/messages/communication', icon: 'communication', show: true},
      // {label: '通知', route: '/messages/notification', icon: 'notification', show: true}
    ],
  }
]

// 响应式导航数据：主题切换图标根据当前主题状态动态显示
const rootNav = computed(() => {
  const themeItems = [
    {
      label: '白天',
      icon: 'son',
      show: !themeStore.isDark,
      action: 'toggleTheme'
    },
    {
      label:'夜晚',
      icon: 'moon',
      show: themeStore.isDark,
      action: 'toggleTheme'
    }
  ]

  return [...baseNav, ...themeItems]
})

// 过滤显示的导航项 - 只显示 show 为 true 的项
const filteredRootNav = computed(() => {
  return rootNav.value.filter(item => {
    // 检查主菜单项是否显示
    if (item.show !== true) {
      return false
    }

    // 如果有子菜单，过滤子菜单
    if (item.children) {
      item.children = item.children.filter(child => child.show === true)
      // 如果子菜单全部被过滤掉，则不显示主菜单
      return item.children.length > 0
    }
    return true
  })
})

// 简单：名称 -> 内联 SVG 字符串（统一 24x24 视口，使用 currentColor）
const icons = {
  chat:`<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 48 48">
<path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" d="M25.875 3.944L29.39 17.23a1.94 1.94 0 0 0 1.38 1.379l13.287 3.515c1.924.51 1.924 3.24 0 3.75L30.769 29.39a1.94 1.94 0 0 0-1.379 1.38l-3.515 13.287c-.51 1.924-3.24 1.924-3.75 0L18.61 30.769a1.94 1.94 0 0 0-1.38-1.379L3.944 25.875c-1.924-.51-1.924-3.24 0-3.75l13.288-3.515a1.94 1.94 0 0 0 1.379-1.38l3.515-13.287c.51-1.924 3.24-1.924 3.75 0"/>
</svg>`,
  research:`
<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
<path fill="currentColor" fill-rule="evenodd" d="M0 3c0-.932 0-1.4.152-1.77C.355.74.744.351 1.232.15c.368-.152.833-.152 1.77-.152s1.4 0 1.77.152c.49.203.879.592 1.08 1.08c.152.368.152.833.152 1.77s0 1.4-.152 1.77c-.203.49-.592.879-1.08 1.08c-.368.152-.833.152-1.77.152s-1.4 0-1.77-.152a2 2 0 0 1-1.08-1.08C0 4.402 0 3.937 0 3m3 2c-.48 0-.789 0-1.03-.017c-.228-.015-.315-.042-.356-.06a1 1 0 0 1-.541-.54c-.017-.042-.044-.128-.06-.356C.998 3.79.998 3.48.998 2.997s0-.79.017-1.03c.015-.228.042-.315.059-.356a1 1 0 0 1 .541-.541c.042-.017.128-.044.356-.06C2.207.994 2.517.994 3 .994s.789 0 1.03.016c.228.016.315.043.356.06c.245.1.44.296.541.54c.017.042.044.129.06.357c.016.237.016.547.016 1.03s0 .789-.017 1.03c-.015.228-.042.315-.059.356a1 1 0 0 1-.541.54c-.042.018-.128.045-.356.06C3.793 5 3.483 5 3 5" clip-rule="evenodd"/><path fill="currentColor" d="M16.5 12a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1zm0 9a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1z"/><path fill="currentColor" fill-rule="evenodd" d="M7.15 10.2c-.152.368-.152.834-.152 1.77s0 1.4.152 1.77c.203.49.592.879 1.08 1.08c.368.152.833.152 1.77.152c.932 0 1.4 0 1.77-.152c.49-.203.879-.592 1.08-1.08c.152-.368.152-.834.152-1.77s0-1.4-.152-1.77a2 2 0 0 0-1.08-1.08c-.368-.152-.834-.152-1.77-.152c-.932 0-1.4 0-1.77.152c-.49.203-.879.592-1.08 1.08m1.82 3.75c.237.016.547.017 1.03.017c.48 0 .789 0 1.03-.017c.228-.016.315-.042.356-.06a1 1 0 0 0 .541-.54c.017-.042.044-.128.06-.356c.016-.237.016-.547.016-1.03s0-.79-.017-1.03c-.015-.228-.042-.315-.059-.356a1 1 0 0 0-.541-.541c-.042-.018-.128-.044-.356-.06A17 17 0 0 0 10 9.96c-.48 0-.789.001-1.03.017c-.228.016-.315.042-.356.06a1 1 0 0 0-.541.54c-.017.042-.044.129-.06.357c-.016.237-.016.547-.016 1.03s0 .789.017 1.03c.015.228.042.315.059.356c.101.245.296.44.541.54c.042.018.128.044.356.06M7 21c0-.932 0-1.4.152-1.77c.203-.49.592-.879 1.08-1.08c.368-.152.833-.152 1.77-.152c.932 0 1.4 0 1.77.152c.49.203.879.592 1.08 1.08c.152.368.152.834.152 1.77s0 1.4-.152 1.77c-.203.49-.592.879-1.08 1.08c-.368.152-.834.152-1.77.152c-.932 0-1.4 0-1.77-.152a2 2 0 0 1-1.08-1.08C7 22.402 7 21.936 7 21m3 2c-.48 0-.789 0-1.03-.017c-.228-.015-.315-.042-.356-.06a1 1 0 0 1-.541-.54c-.017-.042-.044-.128-.06-.356c-.016-.237-.016-.547-.016-1.03s0-.79.017-1.03c.015-.228.042-.315.059-.356a1 1 0 0 1 .541-.541c.042-.017.128-.044.356-.06c.237-.016.547-.016 1.03-.016c.48 0 .789 0 1.03.016c.228.016.315.043.356.06a1 1 0 0 1 .541.54c.017.042.044.129.06.357c.016.237.016.547.016 1.03s0 .789-.017 1.03c-.015.228-.042.315-.059.356a1 1 0 0 1-.541.54c-.042.018-.128.045-.356.06C10.793 23 10.483 23 10 23" clip-rule="evenodd"/><path fill="currentColor" d="M9 3.5a.5.5 0 0 1 .5-.5h14a.5.5 0 0 1 0 1h-14a.5.5 0 0 1-.5-.5M2.5 7a.5.5 0 0 1 .5.5V12h.5a.5.5 0 0 1 0 1H3v7.5a.5.5 0 0 0 .5.5a.5.5 0 0 1 0 1A1.5 1.5 0 0 1 2 20.5v-13a.5.5 0 0 1 .5-.5"/>
</svg>
  `,
  communication:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" color="currentColor"><path d="M22 12.5c0-.491-.005-1.483-.016-1.976c-.065-3.065-.098-4.598-1.229-5.733c-1.131-1.136-2.705-1.175-5.854-1.254a115 115 0 0 0-5.802 0c-3.149.079-4.723.118-5.854 1.254c-1.131 1.135-1.164 2.668-1.23 5.733a69 69 0 0 0 0 2.952c.066 3.065.099 4.598 1.23 5.733c1.131 1.136 2.705 1.175 5.854 1.254q1.204.03 2.401.036"/><path d="m2 6l6.913 3.925c2.526 1.433 3.648 1.433 6.174 0L22 6m0 11.5h-8m8 0c0-.7-1.994-2.008-2.5-2.5m2.5 2.5c0 .7-1.994 2.009-2.5 2.5"/></g>
  </svg>
  `,
  bulletin:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 20 20">
  <g fill="currentColor" fill-rule="evenodd" clip-rule="evenodd"><path d="M6.4 4.882v4.436l7.6 2.073V2.809zm-1 4.436a1 1 0 0 0 .737.965l7.6 2.073A1 1 0 0 0 15 11.39V2.809a1 1 0 0 0-1.263-.965l-7.6 2.073a1 1 0 0 0-.737.965z"/><path d="M3.456 9.3H5.5V4.9H3.453a3.42 3.42 0 0 0 .003 4.4m2.044 1a1 1 0 0 0 1-1V4.9a1 1 0 0 0-1-1H3.253a.55.55 0 0 0-.4.172c-1.602 1.691-1.595 4.353-.002 6.052a.56.56 0 0 0 .405.176z"/><path d="m7.269 10.87l-2.51-.28l-.978 3.91h2.636zm-2.4-1.273a1 1 0 0 0-1.081.75l-.977 3.91a1 1 0 0 0 .97 1.243h2.636a1 1 0 0 0 .974-.772l.852-3.63a1 1 0 0 0-.864-1.223zm13.747-6.374a.5.5 0 0 1-.139.693l-1.5 1a.5.5 0 1 1-.554-.832l1.5-1a.5.5 0 0 1 .693.139M16.2 7.1a.5.5 0 0 1 .5-.5h1.5a.5.5 0 0 1 0 1h-1.5a.5.5 0 0 1-.5-.5m.117 2.23a.5.5 0 0 1 .705-.06l1.38 1.159a.5.5 0 0 1-.643.765l-1.38-1.16a.5.5 0 0 1-.062-.704"/></g>
  </svg>
  `,
  announce:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-miterlimit="10" d="M13.5 13.25L10 15.5l-3.5-2.25v-3.5L10 7.5l3.5 2.25zm-9-6.5L2.5 8l-2-1.25v-2l2-1.25l2 1.25zm15-2L17.5 6l-2-1.25v-2l2-1.25l2 1.25zm-15 16.5l-2 1.25l-2-1.25v-2l2-1.25l2 1.25zm16.5 1l-2 1.25l-2-1.25v-2L19 19l2 1.25zm1.5-9.75l-2 1.25l-2-1.25v-2l2-1.25l2 1.25zm-16-2.75L3.813 7.18m13.883 12.635l-5.968-5.426M18.5 11.5h-5m-5.548 2.684l-4.27 4.555M16.318 5.261l-3.686 3.931"/>
  </svg>
  `,
  moon:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <path fill-opacity="0" d="M15.22 6.03l2.53-1.94L14.56 4L13.5 1l-1.06 3l-3.19.09l2.53 1.94l-.91 3.06l2.63-1.81l2.63 1.81z" fill="currentColor"><animate id="IconifyId1989193d75be32dea38" fill="freeze" attributeName="fill-opacity" begin="0.6s;IconifyId1989193d75be32dea38.begin+6s" dur="0.4s" values="0;1"/><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+2.2s" dur="0.4s" values="1;0"/></path><path fill-opacity="0" d="M13.61 5.25L15.25 4l-2.06-.05L12.5 2l-.69 1.95L9.75 4l1.64 1.25l-.59 1.98l1.7-1.17l1.7 1.17z" fill="currentColor"><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+3s" dur="0.4s" values="0;1"/><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+5.2s" dur="0.4s" values="1;0"/></path><path fill-opacity="0" d="M19.61 12.25L21.25 11l-2.06-.05L18.5 9l-.69 1.95l-2.06.05l1.64 1.25l-.59 1.98l1.7-1.17l1.7 1.17z" fill="currentColor"><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+0.4s" dur="0.4s" values="0;1"/><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+2.8s" dur="0.4s" values="1;0"/></path><path fill-opacity="0" d="M20.828 9.731l1.876-1.439l-2.366-.067L19.552 6l-.786 2.225l-2.366.067l1.876 1.439L17.601 12l1.951-1.342L21.503 12z" fill="currentColor"><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+3.4s" dur="0.4s" values="0;1"/><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+5.6s" dur="0.4s" values="1;0"/></path><g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"><g><path stroke-dasharray="2" stroke-dashoffset="4" d="M12 21v1M21 12h1M12 3v-1M3 12h-1"><animate fill="freeze" attributeName="stroke-dashoffset" dur="0.2s" values="4;2"/></path><path stroke-dasharray="2" stroke-dashoffset="4" d="M18.5 18.5l0.5 0.5M18.5 5.5l0.5 -0.5M5.5 5.5l-0.5 -0.5M5.5 18.5l-0.5 0.5"><animate fill="freeze" attributeName="stroke-dashoffset" begin="0.2s" dur="0.2s" values="4;2"/></path><set fill="freeze" attributeName="opacity" begin="0.5s" to="0"/></g><path d="M7 6 C7 12.08 11.92 17 18 17 C18.53 17 19.05 16.96 19.56 16.89 C17.95 19.36 15.17 21 12 21 C7.03 21 3 16.97 3 12 C3 8.83 4.64 6.05 7.11 4.44 C7.04 4.95 7 5.47 7 6 Z" opacity="0"><set fill="freeze" attributeName="opacity" begin="0.5s" to="1"/></path></g><mask id="IconifyId1989193d75be32dea39"><circle cx="12" cy="12" r="12" fill="#fff"/><circle cx="12" cy="12" r="4"><animate fill="freeze" attributeName="r" begin="0.1s" dur="0.4s" values="4;8"/></circle><circle cx="22" cy="2" r="3" fill="#fff"><animate fill="freeze" attributeName="cx" begin="0.1s" dur="0.4s" values="22;18"/><animate fill="freeze" attributeName="cy" begin="0.1s" dur="0.4s" values="2;6"/><animate fill="freeze" attributeName="r" begin="0.1s" dur="0.4s" values="3;12"/></circle><circle cx="22" cy="2" r="1"><animate fill="freeze" attributeName="cx" begin="0.1s" dur="0.4s" values="22;18"/><animate fill="freeze" attributeName="cy" begin="0.1s" dur="0.4s" values="2;6"/><animate fill="freeze" attributeName="r" begin="0.1s" dur="0.4s" values="1;10"/></circle></mask><circle cx="12" cy="12" r="6" mask="url(#IconifyId1989193d75be32dea39)" fill="currentColor"><animate fill="freeze" attributeName="r" begin="0.1s" dur="0.4s" values="6;10"/><set fill="freeze" attributeName="opacity" begin="0.5s" to="0"/></circle>
  </svg>
  `,
  son:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"><path stroke-dasharray="2" stroke-dashoffset="2" d="M12 19v1M19 12h1M12 5v-1M5 12h-1"><animate fill="freeze" attributeName="d" begin="1.2s" dur="0.2s" values="M12 19v1M19 12h1M12 5v-1M5 12h-1;M12 21v1M21 12h1M12 3v-1M3 12h-1"/><animate fill="freeze" attributeName="stroke-dashoffset" begin="1.2s" dur="0.2s" values="2;0"/></path><path stroke-dasharray="2" stroke-dashoffset="2" d="M17 17l0.5 0.5M17 7l0.5 -0.5M7 7l-0.5 -0.5M7 17l-0.5 0.5"><animate fill="freeze" attributeName="d" begin="1.4s" dur="0.2s" values="M17 17l0.5 0.5M17 7l0.5 -0.5M7 7l-0.5 -0.5M7 17l-0.5 0.5;M18.5 18.5l0.5 0.5M18.5 5.5l0.5 -0.5M5.5 5.5l-0.5 -0.5M5.5 18.5l-0.5 0.5"/><animate fill="freeze" attributeName="stroke-dashoffset" begin="1.4s" dur="0.2s" values="2;0"/></path><animateTransform attributeName="transform" dur="30s" repeatCount="indefinite" type="rotate" values="0 12 12;360 12 12"/></g><g fill="currentColor"><path d="M15.22 6.03L17.75 4.09L14.56 4L13.5 1L12.44 4L9.25 4.09L11.78 6.03L10.87 9.09L13.5 7.28L16.13 9.09L15.22 6.03Z"><animate fill="freeze" attributeName="fill-opacity" dur="0.4s" values="1;0"/></path><path d="M19.61 12.25L21.25 11L19.19 10.95L18.5 9L17.81 10.95L15.75 11L17.39 12.25L16.8 14.23L18.5 13.06L20.2 14.23L19.61 12.25Z"><animate fill="freeze" attributeName="fill-opacity" begin="0.2s" dur="0.4s" values="1;0"/></path></g><path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 6 C7 12.08 11.92 17 18 17 C18.53 17 19.05 16.96 19.56 16.89 C17.95 19.36 15.17 21 12 21 C7.03 21 3 16.97 3 12 C3 8.83 4.64 6.05 7.11 4.44 C7.04 4.95 7 5.47 7 6 Z"><set fill="freeze" attributeName="opacity" begin="0.6s" to="0"/></path><mask id="IconifyId1989193d75be32dea35"><circle cx="12" cy="12" r="12" fill="#fff"/><circle cx="12" cy="12" r="8"><animate fill="freeze" attributeName="r" begin="0.6s" dur="0.4s" values="8;4"/></circle><circle cx="18" cy="6" r="12" fill="#fff"><animate fill="freeze" attributeName="cx" begin="0.6s" dur="0.4s" values="18;22"/><animate fill="freeze" attributeName="cy" begin="0.6s" dur="0.4s" values="6;2"/><animate fill="freeze" attributeName="r" begin="0.6s" dur="0.4s" values="12;3"/></circle><circle cx="18" cy="6" r="10"><animate fill="freeze" attributeName="cx" begin="0.6s" dur="0.4s" values="18;22"/><animate fill="freeze" attributeName="cy" begin="0.6s" dur="0.4s" values="6;2"/><animate fill="freeze" attributeName="r" begin="0.6s" dur="0.4s" values="10;1"/></circle></mask><circle cx="12" cy="12" r="10" mask="url(#IconifyId1989193d75be32dea35)" opacity="0" fill="currentColor"><animate fill="freeze" attributeName="r" begin="0.6s" dur="0.4s" values="10;6"/><set fill="freeze" attributeName="opacity" begin="0.6s" to="1"/></circle>
  </svg>
  `,
  AI: `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
      <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5">
        <path d="M4 16.5a3 3 0 0 0 3 3a2.5 2.5 0 0 0 5 0a2.5 2.5 0 1 0 5 0a3 3 0 0 0 2.567-4.553a3.001 3.001 0 0 0 0-5.893A3 3 0 0 0 17 4.5a2.5 2.5 0 1 0-5 0a2.5 2.5 0 0 0-5 0a3 3 0 0 0-2.567 4.553a3.001 3.001 0 0 0 0 5.893A3 3 0 0 0 4 16.5"/>
        <path d="m7.5 14.5l1.842-5.526a.694.694 0 0 1 1.316 0L12.5 14.5m3-6v6m-7-2h3"/>
      </g>
    </svg>
  `,
  personnel:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 28 28">
  <path fill="currentColor" d="M6.75 3A3.75 3.75 0 0 0 3 6.75v11.5A3.75 3.75 0 0 0 6.75 22h11.5A3.75 3.75 0 0 0 22 18.25V6.75A3.75 3.75 0 0 0 18.25 3zM4.5 6.75A2.25 2.25 0 0 1 6.75 4.5h11.5a2.25 2.25 0 0 1 2.25 2.25v11.5a2.25 2.25 0 0 1-2.25 2.25H6.75a2.25 2.25 0 0 1-2.25-2.25zM9.25 24.5a3.75 3.75 0 0 1-3.021-1.528q.256.028.522.028H18.75A4.25 4.25 0 0 0 23 18.75v-12q0-.264-.028-.52A3.75 3.75 0 0 1 24.5 9.25v9.5a5.75 5.75 0 0 1-5.75 5.75zM7 14.875C7 13.839 7.84 13 8.875 13h7.25C17.16 13 18 13.84 18 14.875c0 1.397-.682 2.465-1.73 3.153c-1.019.669-2.364.972-3.77.972s-2.751-.303-3.77-.972C7.682 17.341 7 16.272 7 14.875m1.875-.375a.375.375 0 0 0-.375.375c0 .84.382 1.459 1.053 1.9c.7.459 1.73.725 2.947.725s2.247-.266 2.947-.726c.671-.44 1.053-1.06 1.053-1.899a.375.375 0 0 0-.375-.375zM11 9a1.5 1.5 0 1 1 3 0a1.5 1.5 0 0 1-3 0m1.5-3a3 3 0 1 0 0 6a3 3 0 0 0 0-6"/>
  </svg>
  `,
  member:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 26 26">
  <g fill="currentColor" fill-rule="evenodd" clip-rule="evenodd"><path d="M6.36 12.977a5.5 5.5 0 0 0-.923 3.05V17a.5.5 0 1 1-1 0v-.972a6.5 6.5 0 0 1 1.092-3.606l.108-.162a.5.5 0 1 1 .832.555z"/><path d="M9.18 11.365c-1.09 0-2.107.544-2.711 1.45l-.832-.554a4.26 4.26 0 0 1 3.542-1.896h.22a.5.5 0 0 1 0 1zm3.078 1.6c.47.706.721 1.534.721 2.382h1a5.3 5.3 0 0 0-.889-2.936l-.1-.15a.5.5 0 1 0-.832.554z"/><path d="M9.448 11.365c1.089 0 2.106.544 2.71 1.45l.832-.554a4.26 4.26 0 0 0-3.542-1.896h-.22a.5.5 0 1 0 0 1z"/><path d="M9.25 10.25a2.25 2.25 0 1 0 0-4.5a2.25 2.25 0 0 0 0 4.5m0 1a3.25 3.25 0 1 0 0-6.5a3.25 3.25 0 0 0 0 6.5m4.259 4.936a5.5 5.5 0 0 0-.924 3.051v1.034a.5.5 0 1 1-1 0v-1.034a6.5 6.5 0 0 1 1.091-3.605l.133-.2a.5.5 0 0 1 .832.556z"/><path d="M16.42 14.5a3.34 3.34 0 0 0-2.78 1.488l-.831-.555A4.34 4.34 0 0 1 16.42 13.5h.224a.5.5 0 1 1 0 1zm3.187 1.686a5.5 5.5 0 0 1 .924 3.051v1.034a.5.5 0 1 0 1 0v-1.034a6.5 6.5 0 0 0-1.092-3.605l-.133-.2a.5.5 0 1 0-.832.556z"/><path d="M16.695 14.5a3.34 3.34 0 0 1 2.78 1.488l.832-.555a4.34 4.34 0 0 0-3.612-1.933h-.225a.5.5 0 1 0 0 1z"/><path d="M16.5 13.5a2.25 2.25 0 1 0 0-4.5a2.25 2.25 0 0 0 0 4.5m0 1a3.25 3.25 0 1 0 0-6.5a3.25 3.25 0 0 0 0 6.5"/><path d="M13 24.5c6.351 0 11.5-5.149 11.5-11.5S19.351 1.5 13 1.5S1.5 6.649 1.5 13S6.649 24.5 13 24.5m0 1c6.904 0 12.5-5.596 12.5-12.5S19.904.5 13 .5S.5 6.096.5 13S6.096 25.5 13 25.5"/></g>
  </svg>
  `,
  personal:`
<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 48 48">
<g fill="none" stroke="currentColor" stroke-linejoin="round" stroke-width="4"><path stroke-linecap="round" d="M24 44c11.046 0 20-8.954 20-20S35.046 4 24 4S4 12.954 4 24s8.954 20 20 20" clip-rule="evenodd"/><path d="M24 23a5 5 0 1 0 0-10a5 5 0 0 0 0 10Z"/><path stroke-linecap="round" d="M10.022 38.332C10.366 33.121 14.702 29 20 29h8c5.291 0 9.623 4.11 9.977 9.311"/></g>
</svg>
  `,
  affairs:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <g fill="none" stroke="currentColor"><path d="M17.5 1.5h-11a5 5 0 0 0-5 5v11a5 5 0 0 0 5 5h11a5 5 0 0 0 5-5v-11a5 5 0 0 0-5-5Z"/><path stroke-linejoin="round" d="M10.5 5.5h-5V17h5zm8 0h-5v7h5z"/></g>
  </svg>
`,
  leave:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 14 14">
  <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"><path d="M5.214 1.643c0-.493.4-.893.893-.893h6.25c.493 0 .893.4.893.893v10.714c0 .493-.4.893-.893.893H9.232"/><path d="M6.553 5.438a1.563 1.563 0 1 0 3.126 0a1.563 1.563 0 1 0-3.126 0m-4.017.669h1.818a1 1 0 0 1 .707.293L7.6 8.94a1 1 0 0 0 .707.292h1.371"/><path d="M6.107 7.446L3.721 9.832a1 1 0 0 1-.707.293H.75"/><path d="m4.321 9.232l1.493 1.493a1 1 0 0 1 .293.707v1.818"/></g>
  </svg>
  `,
  share:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <path fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="1.5" d="M12 3a3 3 0 1 0 3 3m-9.5 9a3 3 0 1 1-3 3m16 3a3 3 0 1 1 3-3M20 13a7.98 7.98 0 0 0-2.708-6M4 13a7.98 7.98 0 0 1 2.708-6M10 20.748A8 8 0 0 0 12 21a8 8 0 0 0 2-.252"/>
  </svg>
  `,
  prize:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 16 16">
  <path fill="currentColor" d="M8 2.57a.5.5 0 0 1 .489.399l.389 1.89l1.92-.214l.087-.002a.5.5 0 0 1 .287.884l-.072.05l-1.68.953l.797 1.76l.028.083a.5.5 0 0 1-.19.528a.5.5 0 0 1-.56.017l-.07-.052l-1.43-1.3l-1.43 1.3a.5.5 0 0 1-.792-.576l.796-1.76l-1.68-.954a.501.501 0 0 1 .302-.932l1.92.214l.39-1.89l.025-.084a.5.5 0 0 1 .465-.315z"/><path fill="currentColor" fill-rule="evenodd" d="M8 0a6.005 6.005 0 0 1 5.127 9.12l1.7 2.55a1 1 0 0 1-.84 1.554l-2.34-.02l-.88 2.17a1.002 1.002 0 0 1-1.76.18l-1.01-1.51l-1.01 1.52a1 1 0 0 1-1.758-.18l-.88-2.17l-2.34.02a.999.999 0 0 1-.84-1.554l1.7-2.56A6.005 6.005 0 0 1 8 .01zM2 12.2l2.68-.023a.5.5 0 0 1 .469.311l1.01 2.49l2-3.01q-.08.004-.161.005c-1.79 0-3.4-.787-4.5-2.03l-1.5 2.26zm10.5-2.26a5.96 5.96 0 0 1-3 1.84l-.893 1.33l1.24 1.85l1.01-2.49a.5.5 0 0 1 .468-.312l2.68.024l-1.5-2.25zM8 .97c-2.76 0-5 2.24-5 5s2.24 5 5 5s5-2.24 5-5s-2.24-5-5-5" clip-rule="evenodd"/>
  </svg>
  `,
  apply:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 20 20">
  <path fill="currentColor" d="M5.5 7.959a3 3 0 1 0-1 0v4.082a3 3 0 1 0 1 0zM5 7a2 2 0 1 1 0-4a2 2 0 0 1 0 4m2 8a2 2 0 1 1-4 0a2 2 0 0 1 4 0m4.146-8.146a.5.5 0 0 0 .708-.708L10.707 5H12.5A2.5 2.5 0 0 1 15 7.5V12a3 3 0 1 0 1 .17V7.5A3.5 3.5 0 0 0 12.5 4h-1.793l1.147-1.146a.5.5 0 0 0-.708-.708l-2 2a.5.5 0 0 0 0 .708zM17 15a2 2 0 1 1-4 0a2 2 0 0 1 4 0"/>
  </svg>
  `,
  tools:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 20 20">
    <path fill="currentColor" d="M15.5 7a2.5 2.5 0 0 1 2.494 2.336L18 9.5v6a2.5 2.5 0 0 1-2.336 2.494L15.5 18h-6a2.5 2.5 0 0 1-2.495-2.336L7 15.5v-6a2.5 2.5 0 0 1 2.336-2.495L9.5 7zm0 1h-6a1.5 1.5 0 0 0-1.493 1.356L8 9.5v6a1.5 1.5 0 0 0 1.356 1.493L9.5 17h6a1.5 1.5 0 0 0 1.493-1.356L17 15.5v-6a1.5 1.5 0 0 0-1.356-1.493zm-2.838-4.305l.048.157L13.285 6h-1.036l-.505-1.889a1.5 1.5 0 0 0-1.696-1.091l-.141.03L4.11 4.604A1.5 1.5 0 0 0 3.02 6.3l.03.142l1.554 5.795A1.5 1.5 0 0 0 6 13.347v1a2.5 2.5 0 0 1-2.31-1.682l-.052-.17l-1.553-5.796a2.5 2.5 0 0 1 1.61-3.014l.158-.047l5.795-1.553a2.5 2.5 0 0 1 2.957 1.458z"/>
  </svg>
  `,
  article:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"><path d="M8 8h8M8 12h8M8 16h5M20 5v14c0 0.55 -0.45 1 -1 1h-14c-0.55 0 -1 -0.45 -1 -1v-14c0 -0.55 0.45 -1 1 -1h14c0.55 0 1 0.45 1 1Z"><animate fill="freeze" attributeName="d" dur="0.4s" values="M8 8h8M8 12h8M8 16h5M20 5v14c0 0.55 -0.45 1 -1 1h-14c-0.55 0 -1 -0.45 -1 -1v-14c0 -0.55 0.45 -1 1 -1h14c0.55 0 1 0.45 1 1Z;M10 6h8M10 10h8M10 14h5M22 3v14c0 0.55 -0.45 1 -1 1h-14c-0.55 0 -1 -0.45 -1 -1v-14c0 -0.55 0.45 -1 1 -1h14c0.55 0 1 0.45 1 1Z"/></path><path stroke-dasharray="36" stroke-dashoffset="36" d="M2 6v15c0 0.55 0.45 1 1 1h15"><animate fill="freeze" attributeName="stroke-dashoffset" begin="0.4s" dur="0.5s" values="36;0"/></path></g>
  </svg>`,
  app:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M2.5 12c0-4.478 0-6.718 1.391-8.109S7.521 2.5 12 2.5c4.478 0 6.718 0 8.109 1.391S21.5 7.521 21.5 12c0 4.478 0 6.718-1.391 8.109S16.479 21.5 12 21.5c-4.478 0-6.718 0-8.109-1.391S2.5 16.479 2.5 12M11 7l1 1.667M17 17l-3.6-6M13 7L7 17m-.5-3h6m5 0h-2" color="currentColor"/>
  </svg>`,
  home: `
    <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
        <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="1.5"><path stroke-linejoin="round" d="M12 22.887c6.012 0 10.887-4.874 10.887-10.887S18.012 1.113 12 1.113S1.113 5.987 1.113 12S5.987 22.887 12 22.887"/><path stroke-linejoin="round" d="M6.43 15.815a1.686 1.686 0 1 0 0-3.373a1.686 1.686 0 0 0 0 3.373m11.358-7.758a3.035 3.035 0 0 1-5.821 1.206a2.18 2.18 0 1 1 .872-3.562a3.035 3.035 0 0 1 4.95 2.356"/><path d="M15.46 17.458c.273-2.51 3.08-1.508 2.96-4.127"/><path stroke-linejoin="round" d="M12 15.005a.296.296 0 1 1 0-.591m0 .591a.296.296 0 1 0 0-.591M5.778 7.373a.296.296 0 0 1 0-.591m0 .591a.296.296 0 0 0 0-.591m7.358 12.793a.296.296 0 0 1 0-.592m0 .592a.296.296 0 0 0 0-.592"/></g>
    </svg>`
  ,
  messages:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <path fill="currentColor" d="M23.963 9.61c-.09-2-1.61-4.821-4.191-6.882A12.25 12.25 0 0 0 12.16.008a12.36 12.36 0 0 0-8.122 2.83A11.33 11.33 0 0 0 .037 10.44c-.18 3.06.3 4.851 1.37 6.082s2.941 2 5.482 2.85c.09 0 .12.15.17.3c.114.51.114 1.04 0 1.55a4.44 4.44 0 0 1-.9 2.081a.43.43 0 0 0 .36.69q.441.01.88-.05a8.8 8.8 0 0 0 3.56-1.16a5.4 5.4 0 0 0 1.001-.95c.69-.84 1.28-1.9 1.62-2.34c.41 0 1.4 0 2.441-.13a13.5 13.5 0 0 0 1.76-.3a8.35 8.35 0 0 0 4.862-3.562a10 10 0 0 0 1.32-5.891m-2 5.481a7.54 7.54 0 0 1-4.352 3.321q-.819.214-1.66.31c-1.32.16-2.4-.07-2.73.17s-.921 1.33-1.561 2.16a5.3 5.3 0 0 1-1.09 1.121a7.8 7.8 0 0 1-3.251 1c.378-.729.597-1.53.64-2.35a3.6 3.6 0 0 0-.18-1.59a1.12 1.12 0 0 0-.64-.7c-2.33-.851-4.001-1.461-5.001-2.661s-1.25-2.681-1-5.362a10.2 10.2 0 0 1 3.7-6.792a11.35 11.35 0 0 1 7.402-2.56a11.2 11.2 0 0 1 6.942 2.4c2.35 1.82 3.81 4.301 3.93 6.102a9.16 9.16 0 0 1-1.21 5.431z"/><path fill="currentColor" d="M5.138 8.779a1.49 1.49 0 0 0 1.35-2.64c-1.43-.85-3.45 1.87-1.35 2.64m1.37 3.391c-1.43-.84-3.45 1.84-1.35 2.65a1.5 1.5 0 0 0 1.35-2.65M19.342 7.3c-.49-.06-1.87-.2-3.471-.29H12.9a40 40 0 0 0-3.462.32a.3.3 0 0 0 .03.6c.71 0 2 .12 3.441.18h2.94c1.591-.1 2.942-.1 3.432-.1a.34.34 0 0 0 .37-.31a.35.35 0 0 0-.31-.4m-.249 5.47H14.3c-.48 0-1 0-1.44.08c-1.631.13-3.002.3-3.322.31a.3.3 0 1 0 0 .601c.36 0 1.73.16 3.401.18h1.44c.44 0 1-.06 1.44-.1a78 78 0 0 0 3.352-.36a.34.34 0 1 0 0-.68z"/>
  </svg>
  `,
  notification: `
     <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
        <path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M14.857 17.082a24 24 0 0 0 5.454-1.31A8.97 8.97 0 0 1 18 9.75V9A6 6 0 0 0 6 9v.75a8.97 8.97 0 0 1-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.3 24.3 0 0 1-5.714 0m5.714 0a3 3 0 1 1-5.714 0M3.124 7.5A8.97 8.97 0 0 1 5.292 3m13.416 0a8.97 8.97 0 0 1 2.168 4.5"/>
     </svg>
  `,

  inbox: `
    <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><g fill="none">
    <path stroke="currentColor" stroke-linecap="round" stroke-width="1.5" d="M10.5 22v-2m4 2v-2"/><path fill="currentColor" d="M11 20v.75h.75V20zm-9.75-8a.75.75 0 0 0 1.5 0zm1.5 4a.75.75 0 0 0-1.5 0zM14 19.25a.75.75 0 0 0 0 1.5zm7.25-8a.75.75 0 0 0 1.5 0zm-3.75-6a.75.75 0 0 0 0 1.5zM22.75 15a.75.75 0 0 0-1.5 0zM7 5.25a.75.75 0 0 0 0 1.5zm2 14a.75.75 0 0 0 0 1.5zm6 1.5a.75.75 0 0 0 0-1.5zm-4-1.5H4.233v1.5H11zm-6.767 0c-.715 0-1.483-.718-1.483-1.855h-1.5c0 1.74 1.231 3.355 2.983 3.355zM6.5 6.75c1.967 0 3.75 1.902 3.75 4.5h1.5c0-3.201-2.246-6-5.25-6zm0-1.5c-3.004 0-5.25 2.799-5.25 6h1.5c0-2.598 1.783-4.5 3.75-4.5zM10.25 17v3h1.5v-3zm0-5.75V17h1.5v-5.75zm-7.5.75v-.75h-1.5V12zm0 5.395V16h-1.5v1.395zm17.043 1.855H14v1.5h5.793zm1.457-1.825c0 1.12-.757 1.825-1.457 1.825v1.5c1.738 0 2.957-1.601 2.957-3.325zm1.5-6.175c0-3.201-2.246-6-5.25-6v1.5c1.967 0 3.75 1.902 3.75 4.5zM21.25 15v2.425h1.5V15zM7 6.75h11v-1.5H7zm2 14h6v-1.5H9z"/><path stroke="currentColor" stroke-linecap="round" stroke-width="1.5" d="M5 16h3m8-6.116V5.411m0 0V2.635c0-.236.168-.439.4-.484l.486-.093a3.2 3.2 0 0 1 1.755.156l.08.03c.554.214 1.16.254 1.737.115a.44.44 0 0 1 .542.427v2.221a.51.51 0 0 1-.393.499l-.066.016a3.2 3.2 0 0 1-1.9-.125a3.2 3.2 0 0 0-1.755-.156z"/></g>
    </svg>
  `,

}

// 图标通过模板内联 SVG 渲染，无需组件或注册表
const currentParent = ref(null)
const activeList = computed(() => (currentParent.value?.children ?? filteredRootNav.value))

function isActive(item) {
  return item.route ? (route.path === item.route || route.path.startsWith(item.route + '/')) : false
}

function onItemClick(item) {
  if (item.children && item.children.length) {
    currentParent.value = item
  } else if (item.action === 'toggleTheme') {
    themeStore.toggleTheme()
  } else if (item.route) {
    router.push(item.route)
  }
}

function goBack() {
  currentParent.value = null
}

// 根据当前路由自动展开对应的父级
watch(
  () => route.path,
  (path) => {
    // 精确匹配路由，避免路径前缀误匹配
    const parent = filteredRootNav.value.find((p) => {
      if (!p.children) return false
      return p.children.some((c) => {
        if (!c.route) return false
        // 精确匹配完整路径，或者匹配以 / 结尾的路径
        return path === c.route || (c.route.endsWith('/') && path.startsWith(c.route))
      })
    })
    currentParent.value = parent ?? null
  },
  {immediate: true}
)
</script>

<template>
  <div class="layout">
    <aside class="sidebar flex flex-col">
      <div v-if="currentParent" class="nav-header">
        <button class="back-btn" @click="goBack" aria-label="返回" title="返回">
          <svg class="back-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path d="M15 6l-6 6l6 6" fill="none" stroke="currentColor" stroke-linecap="round"
                  stroke-linejoin="round" stroke-width="1.7"/>
          </svg>
        </button>
        <span class="nav-header-title">{{ currentParent.label }}</span>
      </div>

      <div class="nav-list ">
        <div
          v-for="item in activeList"
          :key="item.label"
          class="nav-item gap-2"
          :class="{ 'is-active': isActive(item) }"
          :data-icon="item.icon"
          @click="onItemClick(item)"
        >
          <div class="flex items-center">
            <span class="nav-icon" v-html="icons[item.icon] || icons.dot"/>
            <span class="nav-text">{{ item.label }}</span>
          </div>
        </div>
      </div>

      <div class="nav-spacer"/>
    </aside>

    <main class="content">
      <router-view/>
    </main>
  </div>
</template>

<style scoped>

.layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  /* theme tokens - 亮色模式默认值 */
  --accent: #7c3aed; /* violet-600 */
  --accent-2: #6366f1; /* indigo-500 */
  --ring: rgba(99, 102, 241, 0.35);
  --border: rgba(148, 163, 184, 0.25);
  --text: #334155; /* slate-700 */
  --text-dim: #1f61bd; /* slate-600 */

  width: 5rem;
  border-right: 1px solid var(--ring);
  border-radius: 0 0.75rem 0.75rem 0;
  transition: width 0.38s cubic-bezier(.2, .8, .2, 1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 0.85rem 0.5rem 0.75rem;
  gap: 0.35rem;

  background: radial-gradient(120% 90% at 0% 0%, rgba(124, 58, 237, 0.10) 0%, rgba(124, 58, 237, 0.00) 50%),
  radial-gradient(120% 90% at 100% 0%, rgba(99, 102, 241, 0.12) 0%, rgba(99, 102, 241, 0.00) 45%),
  linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0.60));
  backdrop-filter: saturate(140%) blur(12px);
  box-shadow: 0 10px 25px rgba(2, 6, 23, 0.08),
  inset 0 1px 0 rgba(255, 255, 255, 0.35),
  inset 0 -1px 0 rgba(2, 6, 23, 0.03);

  position: sticky;
  top: 0;
  height: 100vh;
}

.sidebar:hover {
  width: 12.5rem;
}

/* 顶部：返回与标题 */
.nav-header {
  display: flex;
  align-items: center;
  gap: 0.55rem;
  padding: 0.2rem 0.35rem 0.45rem 0.35rem;
  margin: 0 0.25rem 0.4rem;
}

.back-btn {
  position: relative;
  display: grid;
  place-items: center;
  width: 2rem;
  height: 2rem;
  border-radius: 9999px;
  color: var(--text-dim);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.65), rgba(255, 255, 255, 0.35));
  border: 1px solid rgba(148, 163, 184, 0.30);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.75), 0 8px 18px rgba(2, 6, 23, 0.06);
  transition: transform 0.22s ease, box-shadow 0.22s ease, color 0.22s ease, background 0.22s ease, border-color 0.22s ease;
  cursor: pointer;
}

.back-btn::before {
  content: "";
  position: absolute;
  inset: -2px;
  border-radius: inherit;
  background: radial-gradient(80% 80% at 30% 20%, rgba(124, 58, 237, 0.35), rgba(99, 102, 241, 0.22));
  filter: blur(8px);
  opacity: 0;
  transition: opacity 0.28s ease;
  z-index: -1;
}

.back-btn:hover {
  color: var(--text);
  transform: translateX(-1px);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.85), 0 10px 22px rgba(2, 6, 23, 0.08);
  border-color: rgba(148, 163, 184, 0.35);
}

.back-btn:hover::before {
  opacity: 1;
}

.back-icon {
  width: 1rem;
  height: 1rem;
}

.nav-header-title {
  display: inline-block;
  max-width: 0;
  opacity: 0;
  transform: translateX(-4px);
  font-size: 0.96rem;
  font-weight: 600;
  color: var(--text);
  letter-spacing: 0.2px;
  white-space: nowrap;
  overflow: hidden;
  transition: max-width 0.38s cubic-bezier(.2, .8, .2, 1), opacity 0.28s ease, transform 0.28s ease;
}

/* 展开时平滑显现标题 */
.sidebar:hover .nav-header-title {
  max-width: 10rem;
  opacity: 1;
  transform: translateX(0);
}

.nav-list {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  margin-top: 0.05rem;
}

.content {
  flex: 1;
  min-width: 0;
  transition: all 0.3s ease;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  padding: 0.6rem 0.7rem;
  margin: 0 0.3rem;
  border-radius: 0.75rem;
  cursor: pointer;
  transition: padding 0.25s ease,
  transform 0.22s ease,
  box-shadow 0.22s ease,
  background-color 0.22s ease,
  border-color 0.22s ease,
  backdrop-filter 0.22s ease;
  border: 1px solid transparent;
  background: transparent;
}

/* 左侧渐变高亮条（更纤细） */
.nav-item::before {
  content: "";
  position: absolute;
  left: 0.35rem;
  top: 22%;
  bottom: 22%;
  width: 3px;
  background: linear-gradient(180deg, var(--accent), var(--accent-2));
  border-radius: 6px;
  opacity: 0;
  transform: translateX(-6px) scaleY(0.6);
  transition: all 0.28s ease;
  filter: drop-shadow(0 1px 4px rgba(99, 102, 241, 0.28));
}

/* hover 背景轻薄填充 */
.nav-item::after {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: radial-gradient(120% 120% at 0% 0%, rgba(124, 58, 237, 0.08), rgba(99, 102, 241, 0.04));
  opacity: 0;
  transform: scale(0.985);
  transition: all 0.22s ease;
  pointer-events: none;
}

.nav-item:hover::before {
  opacity: 1;
  transform: translateX(0) scaleY(1);
}

.nav-item:hover::after {
  opacity: 1;
  transform: scale(1);
}

.nav-item:hover {
  transform: translateX(1px);
  background: rgba(255, 255, 255, 0.35);
  border-color: rgba(148, 163, 184, 0.24);
  backdrop-filter: saturate(125%) blur(7px);
  box-shadow: 0 6px 14px rgba(2, 6, 23, 0.05);
  padding: 0.6rem 0.7rem 0.6rem 0.95rem; /* 增加左侧内边距，拉开竖线与圆框距离 */
}

/* 活跃态（可在路由联动时加 .is-active） */
.nav-item.is-active,
.nav-item.is-active:hover {
  background: linear-gradient(180deg, rgba(124, 58, 237, 0.08), rgba(99, 102, 241, 0.06)),
  rgba(255, 255, 255, 0.6);
  border-color: rgba(99, 102, 241, 0.35);
  box-shadow: 0 10px 22px rgba(79, 70, 229, 0.18);
}

.nav-item.is-active .nav-text {
  font-weight: 600;
}

/* 仅使用名称映射的图标，保持固定尺寸与对齐 */

/* 容器 = 固定尺寸；内部 svg 100% 填充，继承颜色 */
.nav-icon {
  width: 1.35rem;
  height: 1.35rem;
  color: var(--text-dim);
  transition: transform 0.22s ease, color 0.22s ease, filter 0.22s ease, box-shadow 0.22s ease;
  display: block;
  position: relative;
  border-radius: 9999px;
  isolation: isolate;
}

/* 玻璃质感圆底（悬停/激活时显现） */
.nav-icon::before {
  content: "";
  position: absolute;
  left: 50%;
  top: 50%;
  width: 1.9rem;
  height: 1.9rem;
  transform: translate(-50%, -50%) scale(0.86);
  border-radius: inherit;
  background: radial-gradient(120% 120% at 0% 0%, rgba(124, 58, 237, 0.10), rgba(99, 102, 241, 0.05)),
  linear-gradient(180deg, rgba(255, 255, 255, 0.65), rgba(248, 246, 246, 0.35));
  border: 1px solid rgba(148, 163, 184, 0.28);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.55), 0 6px 14px rgba(2, 6, 23, 0.06);
  opacity: 0;
  transition: opacity 0.22s ease, transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
  z-index: -1;
  pointer-events: none;
}

/* 渐变光晕（更柔和） */
.nav-icon::after {
  content: "";
  position: absolute;
  inset: -6px;
  border-radius: inherit;
  background: conic-gradient(from 220deg, rgba(124, 58, 237, 0.55), rgba(99, 102, 241, 0.25), transparent 240deg);
  filter: blur(12px);
  opacity: 0;
  transform: scale(0.92);
  transition: opacity 0.28s ease, transform 0.28s ease, filter 0.28s ease;
  z-index: -2;
  pointer-events: none;
}

.nav-icon :where(svg) {
  width: 100%;
  height: 100%;
  display: block;
}

/* 悬停用中性文本色，激活态主色（沿用原动效） */
.nav-item:hover .nav-icon {
  transform: translateY(-0.5px) scale(1.05);
  color: var(--text);
  filter: drop-shadow(0 2px 6px rgba(2, 6, 23, 0.12));
}

.nav-item:hover .nav-icon::before {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7), 0 8px 18px rgba(2, 6, 23, 0.08);
  border-color: rgba(148, 163, 184, 0.32);
}

.nav-item:hover .nav-icon::after {
  opacity: 0.75;
  transform: scale(1);
}

.nav-item.is-active .nav-icon {
  transform: translateY(-0.5px) scale(1.05);
  color: var(--accent);
  filter: drop-shadow(0 3px 8px rgba(79, 70, 229, 0.25));
}

.nav-item.is-active .nav-icon::before {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8), 0 10px 22px rgba(79, 70, 229, 0.20);
  border-color: rgba(99, 102, 241, 0.38);
}

.nav-item.is-active .nav-icon::after {
  opacity: 1;
  filter: blur(14px);
  transform: scale(1.02);
}

/* 悬停时不展示图标圆框与光晕（提升优先级覆盖下方规则） */
.nav-item:hover .nav-icon::before,
.nav-item:hover .nav-icon::after {
  opacity: 0 !important;
}

/* 点击反馈 */
.nav-item:active .nav-icon {
  transform: translateY(0) scale(0.98);
  filter: drop-shadow(0 1px 4px rgba(2, 6, 23, 0.10));
}

.nav-text {
  display: inline-block;
  max-width: 0;
  margin-left: 0;
  font-size: 0.94rem;
  color: var(--text-dim);
  white-space: nowrap;
  opacity: 0;
  overflow: hidden;
  font-weight: 500;
  letter-spacing: 0.1px;
  transform: translateX(-4px);
  will-change: max-width, opacity, transform;
  transition: max-width 0.38s cubic-bezier(.2, .8, .2, 1), opacity 0.28s ease, color 0.22s ease, transform 0.28s ease;
}

/* 展开时平滑显现文字 */
.sidebar:hover .nav-text {
  max-width: 12rem;
  margin-left: 0.7rem;
  opacity: 1;
  transform: translateX(0);
}

.nav-item:hover .nav-text,
.nav-item.is-active .nav-text {
  color: var(--text);
}

.nav-spacer {
  flex: 1;
}

/* 折叠态（非 hover）视觉优化：保留元素，靠动画过渡 */
.sidebar:not(:hover) .nav-list {
  gap: 0.28rem;
}

.sidebar:not(:hover) .nav-item {
  padding: 0.52rem 0.55rem;
  margin: 0 0.24rem;
  border-color: transparent;
  background: transparent;
  box-shadow: none;
}

/* 不再使用 display:none 和居中对齐，避免跳变 */

/* 暗色模式微调 */
@media (prefers-color-scheme: dark) {
  .sidebar {
    --text: #e5e7eb;
    --text-dim: #cbd5e1;

    background: radial-gradient(120% 90% at 0% 0%, rgba(124, 58, 237, 0.12) 0%, rgba(124, 58, 237, 0.00) 55%),
    radial-gradient(120% 90% at 100% 0%, rgba(99, 102, 241, 0.14) 0%, rgba(99, 102, 241, 0.00) 50%),
    linear-gradient(180deg, rgba(15, 23, 42, 0.65), rgba(15, 23, 42, 0.55));
    border-right: 1px solid rgba(99, 102, 241, 0.35);
    box-shadow: 0 12px 32px rgba(2, 6, 23, 0.55),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  }

  .back-btn {
    background: linear-gradient(180deg, rgba(30, 41, 59, 0.75), rgba(30, 41, 59, 0.55));
    border-color: rgba(148, 163, 184, 0.28);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.06), 0 8px 18px rgba(2, 6, 23, 0.35);
  }

  .back-btn::before {
    background: radial-gradient(80% 80% at 30% 20%, rgba(124, 58, 237, 0.28), rgba(99, 102, 241, 0.18));
  }

  .nav-item:hover,
  .nav-item.is-active {
    background: rgba(30, 41, 59, 0.55);
    border-color: rgba(148, 163, 184, 0.25);
    box-shadow: 0 10px 26px rgba(2, 6, 23, 0.35);
    backdrop-filter: saturate(130%) blur(8px);
  }

  /* 暗色下图标圆底边框与光晕更收敛一些 */
  .nav-icon::before {
    border-color: rgba(148, 163, 184, 0.15);
    background: radial-gradient(120% 120% at 0% 0%, rgba(124, 58, 237, 0.08), rgba(99, 102, 241, 0.04)),
    linear-gradient(180deg, rgba(30, 41, 59, 0.45), rgba(30, 41, 59, 0.25));
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08), 0 8px 16px rgba(2, 6, 23, 0.38);
  }

  .nav-item.is-active .nav-icon::before {
    border-color: rgba(167, 139, 250, 0.4);
    background: radial-gradient(120% 120% at 0% 0%, rgba(167, 139, 250, 0.15), rgba(139, 92, 246, 0.08)),
    linear-gradient(180deg, rgba(30, 41, 59, 0.55), rgba(30, 41, 59, 0.35));
  }

  .nav-icon::after {
    filter: blur(10px);
  }
}

/* 基于 .dark 类的暗色模式样式 */
.dark .sidebar {
  /* 暗色模式CSS变量 */
  --text: #e5e7eb;
  --text-dim: #cbd5e1;
  --ring: rgba(99, 102, 241, 0.35);
  --border: rgba(148, 163, 184, 0.25);

  background: radial-gradient(120% 90% at 0% 0%, rgba(124, 58, 237, 0.08) 0%, rgba(124, 58, 237, 0.00) 55%),
  radial-gradient(120% 90% at 100% 0%, rgba(99, 102, 241, 0.10) 0%, rgba(99, 102, 241, 0.00) 50%),
  linear-gradient(180deg, rgba(0, 0, 0, 0.85), rgba(0, 0, 0, 0.75));
  border-right: 1px solid rgba(99, 102, 241, 0.35);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.75),
  inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.dark .back-btn {
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.6));
  border-color: rgba(148, 163, 184, 0.28);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.06), 0 8px 18px rgba(0, 0, 0, 0.5);
}

.dark .back-btn::before {
  background: radial-gradient(80% 80% at 30% 20%, rgba(124, 58, 237, 0.28), rgba(99, 102, 241, 0.18));
}

.dark .nav-item:hover,
.dark .nav-item.is-active {
  background: rgba(0, 0, 0, 0.6);
  border-color: rgba(148, 163, 184, 0.25);
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.5);
  backdrop-filter: saturate(130%) blur(8px);
}

.dark .nav-icon::before {
  border-color: rgba(148, 163, 184, 0.15);
  background: radial-gradient(120% 120% at 0% 0%, rgba(124, 58, 237, 0.08), rgba(99, 102, 241, 0.04)),
  linear-gradient(180deg, rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.3));
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08), 0 8px 16px rgba(0, 0, 0, 0.5);
}

.dark .nav-item:hover .nav-icon::before {
  border-color: rgba(148, 163, 184, 0.25);
  background: radial-gradient(120% 120% at 0% 0%, rgba(124, 58, 237, 0.12), rgba(99, 102, 241, 0.06)),
  linear-gradient(180deg, rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.4));
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.12), 0 8px 16px rgba(0, 0, 0, 0.5);
}

.dark .nav-item.is-active .nav-icon::before {
  border-color: rgba(167, 139, 250, 0.4);
  background: radial-gradient(120% 120% at 0% 0%, rgba(167, 139, 250, 0.15), rgba(139, 92, 246, 0.08)),
  linear-gradient(180deg, rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.4));
}

.dark .nav-icon::after {
  filter: blur(10px);
}

/* 暗色模式下所有图标的默认颜色 - 更柔和的颜色 */
.dark .nav-icon {
  color: #94a3b8; /* 更柔和的灰色 */
}

/* 暗色模式下悬停时的图标颜色 */
.dark .nav-item:hover .nav-icon {
  color: #e2e8f0; /* 更亮的灰色 */
}

/* 暗色模式下激活状态的图标颜色 */
.dark .nav-item.is-active .nav-icon {
  color: #a78bfa; /* 柔和的紫色 */
}

/* 主题切换图标的响应式颜色 */
/* 亮色模式下显示太阳图标（温暖黄色） */
.nav-item[data-icon="son"] .nav-icon {
  color: #fbbf24;
}

.nav-item[data-icon="son"]:hover .nav-icon {
  color: #f59e0b;
}

/* 暗色模式下显示月亮图标（柔和紫色） */
.nav-item[data-icon="moon"] .nav-icon {
  color: #a78bfa;
}

.nav-item[data-icon="moon"]:hover .nav-icon {
  color: #8b5cf6;
}

/* 暗色模式下的特殊光晕效果 */
.dark .nav-item[data-icon="son"] .nav-icon::after {
  background: conic-gradient(from 220deg, rgba(251, 191, 36, 0.4), rgba(245, 158, 11, 0.25), transparent 240deg);
}

.dark .nav-item[data-icon="moon"] .nav-icon::after {
  background: conic-gradient(from 220deg, rgba(167, 139, 250, 0.4), rgba(139, 92, 246, 0.25), transparent 240deg);
}

/* 暗色模式下激活状态的特殊光晕效果 */
.dark .nav-item.is-active .nav-icon::after {
  background: conic-gradient(from 220deg, rgba(167, 139, 250, 0.4), rgba(139, 92, 246, 0.25), transparent 240deg);
}

/* 仅使用名称映射的图标，保持固定尺寸与对齐 */
</style>
