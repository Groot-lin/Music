<template>
  <div class="chat-container">
    <div class="chat-header">
      <h2>AI助手</h2>
    </div>
    
    <div class="chat-content">
      <div class="chat-messages" ref="messageContainer">
        <div v-for="(message, index) in messages" 
             :key="index" 
             :class="['message', message.type]">
          <div class="avatar">
            {{ message.type === 'user' ? '🎧' : '🎼' }}
          </div>
          <div class="message-content" v-html="formatMessage(message.content)"></div>
        </div>
      </div>
      
      <div class="chat-input-container">
        <div class="chat-input-wrapper">
          <div class="chat-input">
            <input 
              type="text" 
              v-model="inputText" 
              placeholder="请输入您的问题..."
              @keyup.enter="sendMessage"
            >
            <button @click="sendMessage" :disabled="isLoading">
              {{ isLoading ? '发送中...' : '发送' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { post } from '../api/http';  // 移除这行
import {mapGetters} from "vuex";
import {mixin} from "../mixins";

export default {
  name: 'AIChat',
  mixins: [mixin],
  data() {
    return {
      messages: JSON.parse(sessionStorage.getItem('chatMessages') || '[]'),
      inputText: '',
      isLoading: false
    }
  },
  watch: {
    messages: {
      handler(newMessages) {
        sessionStorage.setItem('chatMessages', JSON.stringify(newMessages));
      },
      deep: true
    }
  },
  mounted() {
    // 在window对象上添加处理函数
    window.handleSongClick = (songName) => {
      this.playSong(songName);
    };
    
    // 如果消息列表为空，添加欢迎消息
    if (this.messages.length === 0) {
      this.messages.push({
        type: 'ai',
        content: '你好！我是你的AI音乐助手。我可以帮你：\n1. 推荐音乐\n2. 解答音乐相关问题\n3. 查找歌曲\n4. 分析歌词含义\n\n请告诉我你想了解什么？'
      });
    }
  },
  beforeDestroy() {
    // 清理window对象上的处理函数
    delete window.handleSongClick;
  },
  methods: {
    formatMessage(content) {
      // 使用onclick而不是@click，因为v-html中的Vue事件绑定无效
      return content.replace(/《([^》]+)》/g, '<span class="clickable-song" onclick="handleSongClick(\'$1\')">《$1》</span>').replace(/\n/g, '<br>');
    },

    async playSong(songName) {
      try {
        // 调用后端接口搜索歌曲
        const response = await this.$axios.get(`/song/likeSongOfName?songName=${encodeURIComponent(songName)}`);
        
        if (response.data && response.data.length > 0) {
          // 获取第一首匹配的歌曲
          const song = response.data[0];
          console.log(response);
          
         
        
          this.$message.success(`正在播放: ${songName}`);


          // 设置音乐播放所需的所有状态
          this.$store.commit('setId', song.id);
            const fullUrl = this.$store.state.configure.HOST + song.url;
            console.log('歌曲完整URL:', fullUrl);  // 打印完整URL
            this.$store.commit('setUrl', fullUrl);
            
            const fullPicUrl = this.$store.state.configure.HOST + song.pic;
            console.log('图片完整URL:', fullPicUrl);  // 打印完整图片URL
            this.$store.commit('setPicUrl', fullPicUrl);
            
            this.$store.commit('setListIndex', 0);
            this.$store.commit('setTitle', this.replaceFName(song.name));
            this.$store.commit('setArtist', this.replaceLName(song.name));
            this.$store.commit('setLyric', this.parseLyric(song.lyric));
            
            // 设置播放状态
            this.$store.commit('setIsPlay', true);
            this.$store.commit('setPlayButtonUrl', '#icon-zanting');
            
            // 将整个歌单存入播放列表
            if (this.songList && this.songList.length > 0) {
                this.$store.commit('setListOfSongs', this.songList);
            }
            this.$store.commit('setChangeTime', 0);

        } else {
          this.$message.error(`暂无该歌曲: ${songName},请联系管理员添加`);
        }
      } catch (error) {
        console.error('搜索歌曲失败:', error);
        this.$message.error('搜索歌曲失败，请稍后重试');
      }
    },
    sendMessage() {
      if (!this.inputText.trim() || this.isLoading) return;
      
      // 添加用户消息
      this.messages.push({
        type: 'user',
        content: this.inputText
      });
  
      const userInput = this.inputText;
      this.inputText = '';
      this.isLoading = true;
      
      this.$axios.post("/deepSeek/chat", { message: userInput })
        .then(resp => {
          // 添加AI回复
          console.log(resp);
          this.messages.push({
            type: 'ai',
            content: resp.data
          });
        })
        .catch(error => {
          console.error('Error:', error);
          // 显示错误消息
          this.messages.push({
            type: 'error',
            content: '抱歉，发生了错误，请稍后重试。'
          });
        })
        .finally(() => {
          // 在请求完成后重置loading状态
          this.isLoading = false;
          this.$nextTick(() => {
            this.scrollToBottom();
          });
        });
    },
     scrollToBottom() {
      const container = this.$refs.messageContainer;
      container.scrollTop = container.scrollHeight;
    }
      }
}
</script>

<style scoped>
.chat-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.chat-header {
  text-align: center;
  padding: 20px 0;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.chat-content {
  height: 950px;  /* 设置固定高度 */
  /* 设置最大高度，减去头部和底部空间 */
  /* max-height: calc(100vh - 200px);   */
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.chat-messages {
  height: calc(100% - 100px);  /* 设置消息区域高度，为输入框留出空间 */
  overflow-y: auto;
  padding: 20px;
  margin-bottom: 0;  /* 移除之前的大边距 */
}

.message {
  display: flex;
  align-items: flex-start;
  margin: 20px 0;
  gap: 12px;
}

.message .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  max-width: 800px;  /* 设置固定的最大宽度 */
  width: fit-content;  /* 根据内容自适应宽度 */
  line-height: 1.5;
  white-space: pre-line; /* 添加这行以保持文本换行 */
}

.message.user {
  flex-direction: row-reverse;
}

.message.user .message-content {
  background-color: #007bff;
  color: white;
}

.message.ai .message-content {
  background-color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.message.error .message-content {
  background-color: #ff4444;
  color: white;
  text-align: center;
  margin: 0 auto;
}

.chat-input-container {
  position: fixed;
  bottom: 100px;  /* 从30px改为100px，使输入框上移 */
  left: 50%;
  transform: translateX(-50%);
  width: auto;
  min-width: 600px;
  padding: 20px;
  background: transparent;
}

.chat-input-wrapper {
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.chat-input {
  display: flex;
  gap: 10px;
  padding: 15px 20px;
  align-items: center;
}

.chat-input input {
  flex: 1;
  padding: 12px;
  border: none;
  outline: none;
  font-size: 16px;
  background: transparent;
}

.chat-input button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 15px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.chat-input button:hover {
  background-color: #0056b3;
  transform: translateY(-1px);
}

.chat-input button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  transform: none;
}
.clickable-song {
  color: #007bff;
  cursor: pointer;
  transition: color 0.3s ease;
}

.clickable-song:hover {
  color: #0056b3;
  text-decoration: underline;
}
</style>