<div class="col-lg-8">
  <h2 class="portfolio-modal-title text-secondary text-uppercase mb-0">MC Interactions</h2>
  <div class="divider-custom">
      <div class="divider-custom-line"></div>
      <div class="divider-custom-icon">
          <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-star-fill svg-star" viewBox="0 0 16 16">
              <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"></path>
          </svg>
      </div>
      <div class="divider-custom-line"></div>
  </div>
  <img class="img" src="https://evanward.co.uk/assets/img/portfolio/mc-interactions/thumbnail.webp" alt="...">
  <a href="https://github.com/EvanWard29/MC-Interactions" class=" text-secondary  text-decoration-none" target="_blank">
    <svg xmlns="http://www.w3.org/2000/svg" width="24px" height="24px" fill="currentColor" class="bi bi-github" viewBox="0 0 16 16">
        <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27s1.36.09 2 .27c1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.01 8.01 0 0 0 16 8c0-4.42-3.58-8-8-8"></path>
    </svg>
  </a>
  <div class="text-start mt-3">
    <p>Minecraft Interactions is a Minecraft Java mod I've developed for personal use, inspired by the <a href="https://www.curseforge.com/minecraft/mc-mods/twitchspawn" target="_blank">TwitchSpawn</a> mod.</p>
    <p>The mod was designed to connect to the Twitch API using a WebSocket and listen for subscribed events, such as gaining a new follower or subscriber.</p>
    <img src="https://evanward.co.uk/assets/img/portfolio/mc-interactions/set-night.webp" alt="MC Interactions channel point redemption.">
    <p>Triggered events would then perform an action in-game, like spawning a mob when someone follows, or changing the time of day to night when someone redeems a custom channel point.</p>
    <p class="mb-0">From this project, I learnt a number of skills which I could apply to other projects:</p>
    <ul>
      <li>A much better understaning of Object Oriented Programming.</li>
      <li>Implementing OAuth 2.0 for Twitch to retrieve user access tokens.</li>
      <li>Using <a href="https://central.sonatype.com/artifact/org.java-websocket/Java-WebSocket/1.5.3" target="_blank">Java-WebSocket</a> to connect to Twitch's <a href="https://dev.twitch.tv/docs/eventsub" target="_blank">EventSub API</a> and listen out for event messages.</li>
      <li>Using the <a href="https://sparkjava.com/" target="_blank">Spark</a> framework to setup a minimalist webserver for user authentication.</li>
      <li>Using Cloudflare <a class="text-decoration-none" href="https://www.cloudflare.com/en-gb/products/tunnel/" target="_blank">Tunnels</a> and <a class="text-decoration-none" href="https://www.cloudflare.com/en-gb/application-services/products/dns/" target="_blank">DNS</a> to route traffic.</li>
    </ul>
    <p>The mod was developed as a bespoke project, but at some point in the future, I plan to redesign the mod to be more flexible.</p>
  </div>
</div>
