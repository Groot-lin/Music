<template>
    <div class="content">
       <h1 class="title">
           <slot name="title"></slot>
           <hr/>
       </h1>
       <ul>
           <li>
               <div class="song-item">
                   <span class="item-index"></span>
                   <span class="item-title">歌曲名</span>
                   <span class="item-name">歌手</span>
                   <span class="item-intro">专辑</span>
               </div>
           </li>
           <li v-for="(item,index) in songList" :key="index">
               <div class="song-item" @click="toplay(item.id,item.url,item.pic,index,item.name,item.lyric)">
                   <span class="item-index">
                       {{index + 1}}
                   </span>
                   <span class="item-title">{{replaceFName(item.name)}}</span>
                   <span class="item-name">{{replaceLName(item.name)}}</span>
                   <span class="item-intro">{{item.introduction}}</span>
               </div>
           </li>
       </ul>
    </div>
</template>
<script>
import {mapGetters} from "vuex";
import {mixin} from "../mixins";
export default {
    name: 'album-content',
    mixins: [mixin],
    props:[
        'songList'
    ],
    computed: {
        ...mapGetters([
            'loginIn',              //用户是否已登录
            'userId',               //当前登录用户的id
        ])
    },
    methods: {
        toplay(id, url, pic, index, name, lyric) {
            // 设置音乐播放所需的所有状态
            this.$store.commit('setId', id);
            const fullUrl = this.$store.state.configure.HOST + url;
            console.log('歌曲完整URL:', fullUrl);  // 打印完整URL
            this.$store.commit('setUrl', fullUrl);
            
            const fullPicUrl = this.$store.state.configure.HOST + pic;
            console.log('图片完整URL:', fullPicUrl);  // 打印完整图片URL
            this.$store.commit('setPicUrl', fullPicUrl);
            
            this.$store.commit('setListIndex', index);
            console.log(index);
            
            this.$store.commit('setTitle', this.replaceFName(name));
            this.$store.commit('setArtist', this.replaceLName(name));
            this.$store.commit('setLyric', this.parseLyric(lyric));
            
            // 设置播放状态
            this.$store.commit('setIsPlay', true);
            this.$store.commit('setPlayButtonUrl', '#icon-zanting');
            
            // 将整个歌单存入播放列表
            if (this.songList && this.songList.length > 0) {
                this.$store.commit('setListOfSongs', this.songList);
            }
        }
    }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/album-content.scss';
</style>